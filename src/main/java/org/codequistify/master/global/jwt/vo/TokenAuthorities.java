package org.codequistify.master.global.jwt.vo;

import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;

import java.util.List;

public record TokenAuthorities(PlayerAuthorization authorization) {
    public TokenAuthorities {
        authorization = authorization == null ? PlayerAuthorization.basicPlayer() : authorization;
    }

    public static TokenAuthorities fromStoredAuthorities(List<String> authorities) {
        return new TokenAuthorities(PlayerAuthorization.fromStoredAuthorities(authorities));
    }

    public List<String> storedAuthorities() {
        return authorization.toStoredAuthorities();
    }
}
