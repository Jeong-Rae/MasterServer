package org.codequistify.master.global.jwt.vo;

public record RefreshTokenClaims(TokenAudience audience) {
    public RefreshTokenClaims {
        if (audience == null) {
            throw new IllegalArgumentException("refresh token audience must not be null");
        }
    }
}
