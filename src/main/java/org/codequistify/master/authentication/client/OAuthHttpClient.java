package org.codequistify.master.domain.authentication.client;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.vo.OAuthToken;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.http.WebRequestClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class OAuthHttpClient {
    private final WebRequestClient webRequestClient;

    public OAuthToken requestToken(String tokenUri, MultiValueMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            OAuthToken token =
                    webRequestClient.postForObject(tokenUri, new HttpEntity<>(body, headers), OAuthToken.class);
            if (token == null) {
                throw new NullPointerException("OAuth token response body is null");
            }
            return token;
        } catch (NullPointerException | RestClientException exception) {
            throw communicationFailure(exception);
        }
    }

    public <T> T requestResource(String resourceUri, String accessToken, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            T body = webRequestClient
                    .exchange(resourceUri, HttpMethod.GET, new HttpEntity<>(headers), responseType)
                    .getBody();
            if (body == null) {
                throw new NullPointerException("OAuth resource response body is null");
            }
            return body;
        } catch (NullPointerException | RestClientException exception) {
            throw communicationFailure(exception);
        }
    }

    private BusinessException communicationFailure(RuntimeException exception) {
        return new BusinessException(
                ErrorCode.OAUTH_COMMUNICATION_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR, exception);
    }
}
