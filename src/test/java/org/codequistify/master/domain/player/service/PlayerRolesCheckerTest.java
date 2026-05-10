package org.codequistify.master.domain.player.service;

import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerRolesCheckerTest {

    @Test
    void 역할_목록을_검증하면_단일_전체_일부_포함여부를_반환한다() {
        PlayerRoles roles = PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.PLAYER);

        assertThat(PlayerRolesChecker.checkRole(roles, PlayerRoleType.ADMIN)).isTrue();
        assertThat(PlayerRolesChecker.checkEveryRole(roles, PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.PLAYER))).isTrue();
        assertThat(PlayerRolesChecker.checkAnyRole(roles, PlayerRoles.of(PlayerRoleType.SUPER_ADMIN, PlayerRoleType.PLAYER))).isTrue();
        assertThat(PlayerRolesChecker.checkEveryRole(roles, PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.SUPER_ADMIN))).isFalse();
    }
}
