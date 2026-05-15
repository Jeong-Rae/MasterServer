package org.codequistify.master.domain.authentication.client;

import org.codequistify.master.domain.authentication.vo.OAuthToken;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.http.WebRequestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthHttpClientTest {

    @Mock
    private WebRequestClient webRequestClient;

    private OAuthHttpClient oAuthHttpClient;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        oAuthHttpClient = new OAuthHttpClient(webRequestClient);
    }

    @Test
    void OAuth_Token_요청은_form_urlencoded로_전송한다() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", "authorization-code");
        OAuthToken token = new OAuthToken("access-token", "refresh-token", 1L, 2L, "scope", "bearer", "id-token");
        when(webRequestClient.postForObject(any(String.class), any(HttpEntity.class), any()))
                .thenReturn(token);

        OAuthToken result = oAuthHttpClient.requestToken("https://oauth.example/token", body);

        ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        org.mockito.Mockito.verify(webRequestClient).postForObject(
                org.mockito.ArgumentMatchers.eq("https://oauth.example/token"),
                entityCaptor.capture(),
                org.mockito.ArgumentMatchers.eq(OAuthToken.class));
        assertThat(result).isEqualTo(token);
        assertThat(entityCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_FORM_URLENCODED);
        assertThat(entityCaptor.getValue().getBody().getFirst("code")).isEqualTo("authorization-code");
    }

    @Test
    void OAuth_HTTP_실패는_OAUTH_COMMUNICATION_FAILURE로_변환한다() {
        when(webRequestClient.postForObject(any(String.class), any(HttpEntity.class), any()))
                .thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> oAuthHttpClient.requestToken("https://oauth.example/token", new LinkedMultiValueMap<>()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.OAUTH_COMMUNICATION_FAILURE);
    }
}
