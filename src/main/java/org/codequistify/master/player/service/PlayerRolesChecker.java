package org.codequistify.master.domain.player.service;

import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;

public class PlayerRolesChecker {
    public static boolean checkRole(PlayerRoles roles, PlayerRoleType role) {
        return roles.contains(role);
    }

    public static boolean checkEveryRole(PlayerRoles playerRoles, PlayerRoles roles) {
        return playerRoles.containsAll(roles);
    }

    public static boolean checkAnyRole(PlayerRoles playerRoles, PlayerRoles roles) {
        return playerRoles.containsAny(roles);
    }
}
