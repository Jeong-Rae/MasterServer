package org.codequistify.master.domain.authentication.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.codequistify.master.domain.authentication.dto.LoginResponse;
import org.codequistify.master.domain.authentication.dto.SocialLogInRequest;
import org.codequistify.master.domain.authentication.service.AuthenticationService;
import org.codequistify.master.domain.authentication.service.EmailVerificationService;
import org.codequistify.master.domain.authentication.service.OAuthSignService;
import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.domain.player.dto.PlayerProfile;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.global.jwt.TokenIssuer;
import org.codequistify.master.global.jwt.TokenVerifier;
import org.codequistify.master.global.jwt.cookie.TokenCookieManager;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.codequistify.master.global.util.BasicResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private OAuthSignService oAuthSignService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private PlayerDetailsService playerDetailsService;

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private TokenVerifier tokenVerifier;

    private TokenCookieManager tokenCookieManager;

    private AuthenticationController controller;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("local");
        tokenCookieManager = new TokenCookieManager(environment);
        controller = new AuthenticationController(
                authenticationService,
                oAuthSignService,
                emailVerificationService,
                playerDetailsService,
                tokenIssuer,
                tokenVerifier,
                tokenCookieManager);
    }

    @Test
    void OAuth_로그인_URL_통합_경로는_Provider_타입으로_URL을_반환한다() {
        when(oAuthSignService.loginUrl(OAuthType.GOOGLE)).thenReturn("https://oauth.example/google");

        ResponseEntity<BasicResponse> result = controller.loginUrlOAuth("google");

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody().response()).isEqualTo("https://oauth.example/google");
    }

    @Test
    void OAuth_로그인_통합_경로는_Provider_타입과_Code로_로그인하고_토큰_쿠키를_설정한다() {
        PlayerProfile profile =
                new PlayerProfile("player-id", "oauthname", "oauth@example.com", 0, List.of("ROLE_PLAYER"));
        when(oAuthSignService.login(any(OAuthType.class), any(OAuthCode.class))).thenReturn(profile);
        when(tokenIssuer.issueRefreshToken(any(RefreshTokenClaims.class)))
                .thenReturn(new RefreshToken("refresh-token"));
        when(tokenIssuer.issueAccessToken(any(AccessTokenClaims.class))).thenReturn(new AccessToken("access-token"));

        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        ResponseEntity<LoginResponse> result =
                controller.socialLogInOAuth("google", new SocialLogInRequest("authorization-code"), servletResponse);

        ArgumentCaptor<OAuthCode> codeCaptor = ArgumentCaptor.forClass(OAuthCode.class);
        verify(oAuthSignService).login(org.mockito.ArgumentMatchers.eq(OAuthType.GOOGLE), codeCaptor.capture());
        verify(authenticationService).updateRefreshToken("player-id", "refresh-token");
        assertThat(codeCaptor.getValue().value()).isEqualTo("authorization-code");
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody().profile()).isEqualTo(profile);
        assertThat(result.getBody().token().refreshToken()).isEqualTo("refresh-token");
        assertThat(result.getBody().token().accessToken()).isEqualTo("access-token");
        assertThat(servletResponse.getCookies())
                .extracting("name")
                .containsExactly("POL_REFRESH_TOKEN_DEV", "POL_ACCESS_TOKEN_DEV");
    }
}
