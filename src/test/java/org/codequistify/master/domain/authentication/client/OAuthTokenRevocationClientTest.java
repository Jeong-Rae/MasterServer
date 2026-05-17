package org.codequistify.master.domain.authentication.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.global.http.WebRequestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class OAuthTokenRevocationClientTest {

    @Mock
    private WebRequestClient webRequestClient;

    private OAuthTokenRevocationClient revocationClient;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        revocationClient = new OAuthTokenRevocationClient(webRequestClient);
    }

    @Test
    void Google_OAuth_AccessToken을_revoke_요청한다() {
        revocationClient.revokeGoogle(new OAuthAccessToken("access-token"));

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(webRequestClient)
                .postForEntity(
                        urlCaptor.capture(), any(HttpEntity.class), org.mockito.ArgumentMatchers.eq(String.class));
        assertThat(urlCaptor.getValue()).isEqualTo("https://accounts.google.com/o/oauth2/revoke?token=access-token");
    }

    @Test
    void Google_revoke_실패는_로그아웃_흐름을_막지_않는다() {
        doThrow(new RestClientException("expired"))
                .when(webRequestClient)
                .postForEntity(any(String.class), any(HttpEntity.class), org.mockito.ArgumentMatchers.eq(String.class));

        revocationClient.revokeGoogle(new OAuthAccessToken("expired-token"));

        verify(webRequestClient)
                .postForEntity(any(String.class), any(HttpEntity.class), org.mockito.ArgumentMatchers.eq(String.class));
    }
}
