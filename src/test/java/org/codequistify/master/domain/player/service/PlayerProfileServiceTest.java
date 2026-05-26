package org.codequistify.master.domain.player.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;

import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.persistence.PlayerRepository;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerAuthorizationGuard;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.service.StageSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerProfileServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private StageSearchService stageSearchService;

    @Test
    void 전체_플레이어_조회는_관리자가_아니면_예외를_던진다() {
        PlayerProfileService playerProfileService =
                new PlayerProfileService(playerRepository, stageSearchService, new PlayerAuthorizationGuard());
        PlayerPrincipal principal =
                new PlayerPrincipal(new PlayerId("player-id"), PlayerAuthorization.basicPlayer(), false);
        AuthenticatedPlayer requester = AuthenticatedPlayer.from(principal);

        assertThatThrownBy(() -> playerProfileService.findAllPlayerProfiles(requester))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_PLAYER_PERMISSION.getMessage());
        verifyNoInteractions(playerRepository);
    }

    @Test
    void 내_스테이지_상태_조회에서_NOT_COMPLETED를_요청하면_예외를_던진다() {
        PlayerProfileService playerProfileService =
                new PlayerProfileService(playerRepository, stageSearchService, new PlayerAuthorizationGuard());
        PlayerPrincipal principal =
                new PlayerPrincipal(new PlayerId("player-id"), PlayerAuthorization.basicPlayer(), false);
        AuthenticatedPlayer requester = AuthenticatedPlayer.from(principal);

        assertThatThrownBy(() -> playerProfileService.getMyStagesByStatus(requester, CompletedStatus.NOT_COMPLETED))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_SEARCH_CRITERIA.getMessage());
        verifyNoInteractions(stageSearchService);
    }
}
