package org.codequistify.master.domain.authentication.service;

import com.vane.badwordfiltering.BadWordFiltering;
import org.codequistify.master.domain.authentication.client.OAuthTokenRevocationClient;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.domain.player.security.HashPasswordEncoder;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.domain.player.service.PlayerProfileService;
import org.codequistify.master.global.jwt.TokenIssuer;
import org.codequistify.master.global.jwt.TokenVerifier;
import org.codequistify.master.global.jwt.dto.TokenRequest;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.IssuedTokens;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.codequistify.master.global.jwt.vo.TokenAudience;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationTokenReissueTest {

    @Mock
    private PlayerDetailsService playerDetailsService;

    @Mock
    private PlayerProfileService playerProfileService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private BadWordFiltering badWordFiltering;

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private TokenVerifier tokenVerifier;

    @Mock
    private HashPasswordEncoder hashPasswordEncoder;

    @Mock
    private OAuthTokenRevocationClient oAuthTokenRevocationClient;

    private AuthenticationService authenticationService;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        authenticationService = new AuthenticationService(
                playerDetailsService,
                playerProfileService,
                emailVerificationService,
                badWordFiltering,
                tokenIssuer,
                tokenVerifier,
                hashPasswordEncoder,
                oAuthTokenRevocationClient);
    }

    @Test
    void RefreshToken_재발급은_DB에서_Player를_조회하고_AccessToken_claims로_발급한다() {
        Player player = playerWithRefreshToken("refresh-token");
        when(tokenVerifier.parseRefreshToken("refresh-token"))
                .thenReturn(new RefreshTokenClaims(TokenAudience.from("player-id")));
        when(playerDetailsService.findOnePlayerById("player-id")).thenReturn(player);
        when(tokenIssuer.issueAccessToken(any(AccessTokenClaims.class))).thenReturn(new AccessToken("access-token"));

        IssuedTokens result = authenticationService.reissueAccessToken(new TokenRequest("refresh-token"));

        ArgumentCaptor<AccessTokenClaims> claimsCaptor = ArgumentCaptor.forClass(AccessTokenClaims.class);
        verify(playerDetailsService).findOnePlayerById("player-id");
        verify(tokenIssuer).issueAccessToken(claimsCaptor.capture());
        assertThat(claimsCaptor.getValue().audience().value()).isEqualTo("player-id");
        assertThat(result.refreshToken().value()).isEqualTo("refresh-token");
        assertThat(result.accessToken().value()).isEqualTo("access-token");
    }

    @Test
    void 저장된_RefreshToken과_다르면_AccessToken을_발급하지_않는다() {
        Player player = playerWithRefreshToken("saved-refresh-token");
        when(tokenVerifier.parseRefreshToken("request-refresh-token"))
                .thenReturn(new RefreshTokenClaims(TokenAudience.from("player-id")));
        when(playerDetailsService.findOnePlayerById("player-id")).thenReturn(player);

        IssuedTokens result = authenticationService.reissueAccessToken(new TokenRequest("request-refresh-token"));

        verify(tokenIssuer, never()).issueAccessToken(any(AccessTokenClaims.class));
        assertThat(result.refreshToken().value()).isEqualTo("request-refresh-token");
        assertThat(result.accessToken().value()).isEmpty();
    }

    private Player playerWithRefreshToken(String refreshToken) {
        return Player.create(
                new PlayerId("player-id"),
                new PlayerProfile(
                        new PlayerName("pol"),
                        new PlayerEmail("pol@example.com"),
                        new PlayerExperience(0)),
                new PlayerAuthentication(
                        new HashedPassword(""),
                        null,
                        new RefreshToken(refreshToken),
                        null),
                PlayerAuthorization.basicPlayer());
    }
}
