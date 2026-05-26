package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PlayerAuthorizationGuard {

    public boolean isAdmin(AuthenticatedPlayer requester) {
        return requester
                .authorization()
                .roles()
                .containsAny(PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.SUPER_ADMIN));
    }

    public void requireAdmin(AuthenticatedPlayer requester) {
        if (!isAdmin(requester)) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_PLAYER_PERMISSION, HttpStatus.FORBIDDEN);
        }
    }
}
