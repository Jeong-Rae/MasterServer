package org.codequistify.master.domain.player.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.*;
import org.codequistify.master.domain.player.dto.UpdatePasswordRequest;
import org.codequistify.master.domain.player.persistence.PlayerRepository;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerDetailsServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerDetailsService playerDetailsService;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        playerDetailsService =
                new PlayerDetailsService(playerRepository, new PlayerPrincipalMapper(), new HashPasswordEncoder());
    }

    @Test
    void 비밀번호_초기화에서_정책에_맞지_않는_비밀번호를_요청하면_예외를_던진다() {
        PlayerPrincipal principal =
                new PlayerPrincipal(new PlayerId("player-id"), PlayerAuthorization.basicPlayer(), false);
        UpdatePasswordRequest request = new UpdatePasswordRequest(null, "short");

        assertThatThrownBy(() -> playerDetailsService.resetPassword(AuthenticatedPlayer.from(principal), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PASSWORD_POLICY_VIOLATION.getMessage());
        verifyNoInteractions(playerRepository);
    }

    @Test
    void 비밀번호_변경에서_정책에_맞지_않는_비밀번호를_요청하면_예외를_던진다() {
        PlayerPrincipal principal =
                new PlayerPrincipal(new PlayerId("player-id"), PlayerAuthorization.basicPlayer(), false);
        UpdatePasswordRequest request = new UpdatePasswordRequest("password99763892*", "short");

        assertThatThrownBy(() -> playerDetailsService.updatePassword(AuthenticatedPlayer.from(principal), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PASSWORD_POLICY_VIOLATION.getMessage());
        verifyNoInteractions(playerRepository);
    }

    @Test
    void 플레이어_비밀번호를_비교하면_비크립트_접두사를_제외한_본문으로_검증한다() {
        HashPasswordEncoder hashPasswordEncoder = new HashPasswordEncoder();
        Player player = Player.create(
                PlayerId.newId(),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(0)),
                PlayerAuthentication.local(hashPasswordEncoder.encode(RawPassword.middleLevel("password99763892*"))),
                PlayerAuthorization.basicPlayer());

        boolean result = playerDetailsService.matchesPassword("password99763892*", player);

        assertThat(result).isTrue();
    }
}
