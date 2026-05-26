package org.codequistify.master.domain.authentication.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.shared.http.WebRequestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthTokenRevocationClient {
    private static final String GOOGLE_REVOKE_URI = "https://accounts.google.com/o/oauth2/revoke?token=%s";

    private final Logger LOGGER = LoggerFactory.getLogger(OAuthTokenRevocationClient.class);
    private final WebRequestClient webRequestClient;

    public void revokeGoogle(OAuthAccessToken accessToken) {
        if (accessToken.isBlank()) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            webRequestClient.postForEntity(revokeUri(accessToken), request, String.class);
        } catch (RuntimeException exception) {
            LOGGER.info("[revokeGoogle] 만료시킬 수 없는 토큰");
        }
    }

    private String revokeUri(OAuthAccessToken accessToken) {
        return GOOGLE_REVOKE_URI.formatted(URLEncoder.encode(accessToken.value(), StandardCharsets.UTF_8));
    }
}
