package org.codequistify.master.domain.player.security;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.Test;

class PlayerAuthorizationGuardTest {

    private final PlayerAuthorizationGuard playerAuthorizationGuard = new PlayerAuthorizationGuard();

    @Test
    void 관리자_요구_검증은_ADMIN_또는_SUPER_ADMIN이면_통과한다() {
        AuthenticatedPlayer admin = new AuthenticatedPlayer(
                new PlayerId("admin-id"), new PlayerAuthorization(PlayerRoles.of(PlayerRoleType.ADMIN), null));

        assertThatCode(() -> playerAuthorizationGuard.requireAdmin(admin)).doesNotThrowAnyException();
    }

    @Test
    void 관리자_요구_검증은_일반_사용자이면_예외를_던진다() {
        AuthenticatedPlayer player =
                new AuthenticatedPlayer(new PlayerId("player-id"), PlayerAuthorization.basicPlayer());

        assertThatThrownBy(() -> playerAuthorizationGuard.requireAdmin(player))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_PLAYER_PERMISSION.getMessage());
    }
}
