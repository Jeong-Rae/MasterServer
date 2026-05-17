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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class GoogleOAuthStrategy implements OAuthStrategy {
    private final Logger LOGGER = LoggerFactory.getLogger(GoogleOAuthStrategy.class);
    private final OAuthHttpClient oAuthHttpClient;
    private final OAuthKey oAuthKey;

    @Override
    public OAuthType type() {
        return OAuthType.GOOGLE;
    }

    @Override
    public String loginUrl() {
        return "https://accounts.google.com/o/oauth2/auth?" + "client_id="
                + oAuthKey.getGOOGLE_CLIENT_ID() + "&redirect_uri="
                + oAuthKey.getGOOGLE_REDIRECT_URI() + "&response_type=code"
                + "&scope=email%20profile";
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
        body.add("client_id", oAuthKey.getGOOGLE_CLIENT_ID());
        body.add("client_secret", oAuthKey.getGOOGLE_CLIENT_SECRET());
        body.add("redirect_uri", oAuthKey.getGOOGLE_REDIRECT_URI());
        body.add("grant_type", "authorization_code");

        OAuthToken response = oAuthHttpClient.requestToken(oAuthKey.getGOOGLE_TOKEN_URI(), body);
        LOGGER.info("[getOAuthToken] token 정보{}", response);
        return response;
    }

    @LogMethodInvocation
    private OAuthResource getUserResource(String accessToken) {
        return oAuthHttpClient.requestResource(oAuthKey.getGOOGLE_RESOURCE_URI(), accessToken, OAuthResource.class);
    }
}
