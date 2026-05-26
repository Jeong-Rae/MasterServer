package org.codequistify.master.domain.authentication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.authentication.vo.OAuthResource;
import org.codequistify.master.domain.authentication.vo.OAuthToken;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthIdentity;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.OAuthAccessToken;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OAuthSignServiceTest {

    @Mock
    private PlayerDetailsService playerDetailsService;

    private StubOAuthStrategy strategy;
    private OAuthSignService oAuthSignService;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        strategy = new StubOAuthStrategy(OAuthType.GOOGLE);
        oAuthSignService = new OAuthSignService(new OAuthStrategies(List.of(strategy)), playerDetailsService);
    }

    @Test
    void OAuth_로그인_URL은_Provider_Strategy가_생성한다() {
        String result = oAuthSignService.loginUrl(OAuthType.GOOGLE);

        assertThat(result).isEqualTo("https://oauth.example/google");
    }

    @Test
    void 기존_가입자는_OAuth_AccessToken을_갱신하고_Profile을_반환한다() {
        Player player = player("existing-id", "google-id", "");
        when(playerDetailsService.findOnePlayerByEmail("oauth@example.com")).thenReturn(player);
        when(playerDetailsService.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        org.codequistify.master.domain.player.dto.PlayerProfileResponse result =
                oAuthSignService.login(OAuthType.GOOGLE, new OAuthCode("authorization-code"));

        assertThat(result.id()).isEqualTo("existing-id");
        assertThat(result.email()).isEqualTo("oauth@example.com");
        verify(playerDetailsService, never()).checkOAuthType("oauth@example.com");
        verify(playerDetailsService).save(any(Player.class));
    }

    @Test
    void 미가입자는_가입한_뒤_OAuth_AccessToken을_갱신하고_Profile을_반환한다() {
        when(playerDetailsService.findOnePlayerByEmail("oauth@example.com"))
                .thenThrow(new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.BAD_REQUEST));
        when(playerDetailsService.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        org.codequistify.master.domain.player.dto.PlayerProfileResponse result =
                oAuthSignService.login(OAuthType.GOOGLE, new OAuthCode("authorization-code"));

        assertThat(result.email()).isEqualTo("oauth@example.com");
        assertThat(result.name()).isEqualTo("oauthname");
        verify(playerDetailsService).save(any(Player.class));
    }

    @Test
    void Strategy의_OAuth_통신_예외는_전파한다() {
        strategy.fail();

        assertThatThrownBy(() -> oAuthSignService.login(OAuthType.GOOGLE, new OAuthCode("authorization-code")))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.OAUTH_COMMUNICATION_FAILURE.getMessage());
    }

    private Player player(String id, String oauthId, String accessToken) {
        return new Player(
                new PlayerId(id),
                new PlayerProfile(
                        new PlayerName("oauthname"), new PlayerEmail("oauth@example.com"), new PlayerExperience(0)),
                new PlayerAuthentication(
                        new HashedPassword(""),
                        OAuthIdentity.of(OAuthType.GOOGLE, oauthId),
                        new RefreshToken(""),
                        new OAuthAccessToken(accessToken)),
                PlayerAuthorization.basicPlayer(),
                org.codequistify.master.domain.player.domain.PlayerStatus.ACTIVE);
    }

    private record StubOAuthStrategy(OAuthType type) implements OAuthStrategy {
        private static boolean failed;

        private StubOAuthStrategy {
            failed = false;
        }

        @Override
        public String loginUrl() {
            return "https://oauth.example/" + type.name().toLowerCase();
        }

        @Override
        public OAuthData fetchOAuthData(OAuthCode code) {
            if (failed) {
                throw new BusinessException(ErrorCode.OAUTH_COMMUNICATION_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return OAuthData.of(
                    new OAuthToken("access-token", "refresh-token", 1L, 2L, "scope", "bearer", "id-token"),
                    new OAuthResource("google-id", "oauth@example.com", "oauthname"));
        }

        private void fail() {
            failed = true;
        }
    }
}
