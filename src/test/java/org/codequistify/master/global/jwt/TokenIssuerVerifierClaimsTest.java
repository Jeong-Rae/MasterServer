package org.codequistify.master.global.jwt;

import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.codequistify.master.global.jwt.vo.TokenAudience;
import org.codequistify.master.global.jwt.vo.TokenAuthorities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenIssuerVerifierClaimsTest {
    private TokenIssuer tokenIssuer;
    private TokenVerifier tokenVerifier;

    @BeforeEach
    void 토큰_발급기와_검증기를_준비한다() {
        TokenSigningKey signingKey = new TokenSigningKey();
        ReflectionTestUtils.setField(signingKey, "jwtSecret", "12345678901234567890123456789012");
        signingKey.init();
        tokenIssuer = new TokenIssuer(signingKey);
        tokenVerifier = new TokenVerifier(signingKey);
    }

    @Test
    void AccessToken은_claims_모델로_발급하고_복원한다() {
        AccessTokenClaims claims = new AccessTokenClaims(
                TokenAudience.from("player-id"),
                TokenAuthorities.fromStoredAuthorities(List.of(
                        PlayerRoleType.PLAYER.getRole(),
                        PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission())));

        AccessToken token = tokenIssuer.issueAccessToken(claims);
        AccessTokenClaims result = tokenVerifier.parseAccessToken(token.value());

        assertThat(result.audience().value()).isEqualTo("player-id");
        assertThat(result.authorities().storedAuthorities())
                .containsExactly(PlayerRoleType.PLAYER.getRole(), PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission());
    }

    @Test
    void RefreshToken은_claims_모델로_발급하고_복원한다() {
        RefreshTokenClaims claims = new RefreshTokenClaims(TokenAudience.from("player-id"));

        RefreshToken token = tokenIssuer.issueRefreshToken(claims);
        RefreshTokenClaims result = tokenVerifier.parseRefreshToken(token.value());

        assertThat(result.audience().value()).isEqualTo("player-id");
    }
}
