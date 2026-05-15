package org.codequistify.master.global.jwt.dto;

import org.codequistify.master.global.jwt.vo.IssuedTokens;

public record TokenResponse(
        String refreshToken,
        String accessToken
) {
    public static TokenResponse from(IssuedTokens tokens) {
        return new TokenResponse(tokens.refreshToken().value(), tokens.accessToken().value());
    }
}
