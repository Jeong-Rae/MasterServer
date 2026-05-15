package org.codequistify.master.global.jwt.vo;

import org.codequistify.master.domain.player.domain.vo.PlayerId;

public record TokenAudience(PlayerId id) {
    public TokenAudience {
        if (id == null) {
            throw new IllegalArgumentException("token audience must not be null");
        }
    }

    public static TokenAudience from(String value) {
        return new TokenAudience(new PlayerId(value));
    }

    public String value() {
        return id.value();
    }
}
