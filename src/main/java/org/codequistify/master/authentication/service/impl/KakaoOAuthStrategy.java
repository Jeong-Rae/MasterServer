package org.codequistify.master.domain.authentication.service.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.client.OAuthHttpClient;
import org.codequistify.master.domain.authentication.config.OAuthKey;
import org.codequistify.master.domain.authentication.service.OAuthStrategy;
import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.authentication.vo.OAuthResource;
import org.codequistify.master.domain.authentication.vo.OAuthToken;
import org.codequistify.master.domain.authentication.vo.ResourceOfKakao;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.global.aspect.LogMethodInvocation;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class KakaoOAuthStrategy implements OAuthStrategy {
    private final Logger LOGGER = LoggerFactory.getLogger(KakaoOAuthStrategy.class);
    private final OAuthHttpClient oAuthHttpClient;
    private final OAuthKey oAuthKey;

    @Override
    public OAuthType type() {
        return OAuthType.KAKAO;
    }

    @Override
    public String loginUrl() {
        return "https://kauth.kakao.com/oauth/authorize?response_type=code" + "&client_id="
                + oAuthKey.KAKAO_CLIENT_ID + "&redirect_uri="
                + oAuthKey.KAKAO_REDIRECT_URI;
    }

    @Override
    @LogMethodInvocation
    public OAuthData fetchOAuthData(OAuthCode code) {
        OAuthToken token = getOAuthToken(code);
        OAuthResource resource = getUserResource(token.access_token());
        return OAuthData.of(token, resource);
    }

    @LogMethodInvocation
    private OAuthToken getOAuthToken(OAuthCode code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code.value());
        body.add("client_id", oAuthKey.KAKAO_CLIENT_ID);
        body.add("redirect_uri", oAuthKey.KAKAO_REDIRECT_URI);
        body.add("grant_type", "authorization_code");

        OAuthToken response = oAuthHttpClient.requestToken(oAuthKey.KAKAO_TOKEN_URI, body);
        LOGGER.info("[getOAuthToken] token 정보{}", response);
        return response;
    }

    @LogMethodInvocation
    private OAuthResource getUserResource(String accessToken) {
        try {
            Map<String, String> map = oAuthHttpClient
                    .requestResource(oAuthKey.KAKAO_RESOURCE_URI, accessToken, ResourceOfKakao.class)
                    .properties();
            OAuthResource response = new OAuthResource(map.get("id"), map.get("email"), map.get("nickname"));
            LOGGER.info("[getUserResource] 리소스: {}", response);
            return response;
        } catch (NullPointerException exception) {
            LOGGER.info("[getUserResource] 정보 요청 실패");
            throw new BusinessException(ErrorCode.OAUTH_COMMUNICATION_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
