package org.codequistify.master.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuer {
    private static final Long ACCESS_VALIDITY_TIME = 60 * 60 * 1000L;
    private static final Long REFRESH_VALIDITY_TIME = 7 * 24 * 60 * 60 * 1000L;

    private final TokenSigningKey signingKey;
    private final Logger LOGGER = LoggerFactory.getLogger(TokenIssuer.class);

    public AccessToken issueAccessToken(AccessTokenClaims accessTokenClaims) {
        Claims claims = Jwts.claims();
        claims.put(
                TokenConstants.AUTHORITIES_CLAIM,
                accessTokenClaims.authorities().storedAuthorities());
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setAudience(accessTokenClaims.audience().value())
                .setIssuedAt(now)
                .setIssuer(TokenConstants.ISSUER)
                .setExpiration(new Date(now.getTime() + ACCESS_VALIDITY_TIME))
                .signWith(signingKey.value())
                .compact();

        LOGGER.info("[issueAccessToken] {}", token);
        return new AccessToken(token);
    }

    public AccessToken issueTempToken(String email) {
        Claims claims = Jwts.claims();
        claims.put(TokenConstants.AUTHORITIES_CLAIM, List.of(PlayerRoleType.TEMPORARY.getRole()));
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setAudience(email)
                .setIssuedAt(now)
                .setIssuer(TokenConstants.ISSUER)
                .setExpiration(new Date(now.getTime() + ACCESS_VALIDITY_TIME / 6))
                .signWith(signingKey.value())
                .compact();

        LOGGER.info("[issueTempToken] {}", token);
        return new AccessToken(token);
    }

    public RefreshToken issueRefreshToken(RefreshTokenClaims refreshTokenClaims) {
        Claims claims = Jwts.claims();
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setAudience(refreshTokenClaims.audience().value())
                .setIssuedAt(now)
                .setIssuer(TokenConstants.ISSUER)
                .setExpiration(new Date(now.getTime() + REFRESH_VALIDITY_TIME))
                .signWith(signingKey.value())
                .compact();

        LOGGER.info("[issueRefreshToken] {}", token);
        return new RefreshToken(token);
    }
}
