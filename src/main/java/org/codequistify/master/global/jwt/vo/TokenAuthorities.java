package org.codequistify.master.global.jwt.vo;

import java.util.List;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;

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
