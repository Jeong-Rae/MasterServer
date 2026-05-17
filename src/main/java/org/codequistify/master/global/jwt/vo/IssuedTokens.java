package org.codequistify.master.global.jwt.vo;

import org.codequistify.master.domain.player.domain.vo.RefreshToken;

public record IssuedTokens(RefreshToken refreshToken, AccessToken accessToken) {
    public IssuedTokens {
        refreshToken = refreshToken == null ? new RefreshToken("") : refreshToken;
        accessToken = accessToken == null ? new AccessToken("") : accessToken;
    }

    public static IssuedTokens of(RefreshToken refreshToken, AccessToken accessToken) {
        return new IssuedTokens(refreshToken, accessToken);
    }

    public static IssuedTokens accessOnly(AccessToken accessToken) {
        return new IssuedTokens(new RefreshToken(""), accessToken);
    }
}
