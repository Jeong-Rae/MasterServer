package org.codequistify.master.domain.authentication.service.impl;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.client.OAuthHttpClient;
import org.codequistify.master.domain.authentication.service.OAuthStrategy;
import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.authentication.vo.OAuthResource;
import org.codequistify.master.domain.authentication.vo.OAuthToken;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.global.aspect.LogMethodInvocation;
import org.codequistify.master.global.config.OAuthKey;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GithubOAuthStrategy implements OAuthStrategy {
    private final Logger LOGGER = LoggerFactory.getLogger(GithubOAuthStrategy.class);
    private final OAuthHttpClient oAuthHttpClient;
    private final OAuthKey oAuthKey;

    @Override
    public OAuthType type() {
        return OAuthType.GITHUB;
    }

    @Override
    public String loginUrl() {
        return "https://github.com/login/oauth/authorize?" +
                "client_id=" + oAuthKey.getGITHUB_CLIENT_ID() +
                "&redirect_uri=" + URLEncoder.encode(oAuthKey.getGITHUB_REDIRECT_URI(), StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&state=" + URLEncoder.encode("pol", StandardCharsets.UTF_8);
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
        body.add("client_id", oAuthKey.getGITHUB_CLIENT_ID());
        body.add("client_secret", oAuthKey.getGITHUB_CLIENT_SECRET());
        body.add("grant_type", "authorization_code");
        body.add("state", "pol");

        OAuthToken response = oAuthHttpClient.requestToken(oAuthKey.getGITHUB_TOKEN_URI(), body);
        LOGGER.info("[getAccessToken] token 정보 {}", response);
        return response;
    }

    @LogMethodInvocation
    private OAuthResource getUserResource(String accessToken) {
        try {
            Map<String, String> map = oAuthHttpClient
                    .requestResource(oAuthKey.getGITHUB_RESOURCE_URI(), accessToken, HashMap.class);
            OAuthResource response = new OAuthResource(String.valueOf(map.get("id")), map.get("login"), map.get("name"));
            LOGGER.info("[getUserResource] 리소스: {}", response);
            return response;
        } catch (NullPointerException exception) {
            LOGGER.info("[getUserResource] 정보 요청 실패 {}", exception.getMessage());
            throw new BusinessException(ErrorCode.OAUTH_COMMUNICATION_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
