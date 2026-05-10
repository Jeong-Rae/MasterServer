package org.codequistify.master.domain.player.service;

import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerRolesCheckerTest {

    @Test
    void checksRolesWithDomainTypes() {
        PlayerRoles roles = PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.PLAYER);

        assertTrue(PlayerRolesChecker.checkRole(roles, PlayerRoleType.ADMIN));
        assertTrue(PlayerRolesChecker.checkEveryRole(roles, PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.PLAYER)));
        assertTrue(PlayerRolesChecker.checkAnyRole(roles, PlayerRoles.of(PlayerRoleType.SUPER_ADMIN, PlayerRoleType.PLAYER)));
        assertFalse(PlayerRolesChecker.checkEveryRole(roles, PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.SUPER_ADMIN)));
    }
}
