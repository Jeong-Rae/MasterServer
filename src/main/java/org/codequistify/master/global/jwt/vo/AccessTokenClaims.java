package org.codequistify.master.global.jwt.vo;

import org.codequistify.master.domain.player.security.PlayerPrincipal;

public record AccessTokenClaims(TokenAudience audience, TokenAuthorities authorities) {
    public AccessTokenClaims {
        if (audience == null || authorities == null) {
            throw new IllegalArgumentException("access token claims must not be null");
        }
    }

    public PlayerPrincipal toPrincipal() {
        return new PlayerPrincipal(audience.id(), authorities.authorization(), false);
    }
}
