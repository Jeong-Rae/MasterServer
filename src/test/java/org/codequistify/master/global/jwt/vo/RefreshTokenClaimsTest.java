package org.codequistify.master.global.jwt.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenClaimsTest {

    @Test
    void RefreshToken_claims는_audience만_가진다() {
        RefreshTokenClaims claims = new RefreshTokenClaims(TokenAudience.from("player-id"));

        assertThat(claims.audience().value()).isEqualTo("player-id");
    }
}
