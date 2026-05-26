package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.vo.PlayerId;

public record AuthenticatedPlayer(PlayerId id, PlayerAuthorization authorization) {
    public AuthenticatedPlayer {
        if (id == null) {
            throw new IllegalArgumentException("authenticated player id must not be null");
        }
        authorization = authorization == null ? PlayerAuthorization.basicPlayer() : authorization;
    }

    public static AuthenticatedPlayer from(PlayerPrincipal principal) {
        return new AuthenticatedPlayer(principal.id(), principal.authorization());
    }
}
