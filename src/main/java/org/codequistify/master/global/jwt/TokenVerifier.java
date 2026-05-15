package org.codequistify.master.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.codequistify.master.global.jwt.vo.TokenAudience;
import org.codequistify.master.global.jwt.vo.TokenAuthorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenVerifier {
    private final TokenSigningKey signingKey;
    private final Logger LOGGER = LoggerFactory.getLogger(TokenVerifier.class);

    public AccessTokenClaims parseAccessToken(String token) {
        Claims claims = parseClaims(token);
        List<String> authorities = claims.get(TokenConstants.AUTHORITIES_CLAIM, List.class);
        return new AccessTokenClaims(
                TokenAudience.from(claims.getAudience()),
                TokenAuthorities.fromStoredAuthorities(authorities));
    }

    public RefreshTokenClaims parseRefreshToken(String token) {
        Claims claims = parseClaims(token);
        return new RefreshTokenClaims(TokenAudience.from(claims.getAudience()));
    }

    public Claims getClaims(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey.value())
                    .build()
                    .parseClaimsJws(token);

            return claims.getBody();
        } catch (IllegalArgumentException exception) {
            LOGGER.info("잘못된 JWT 토큰");
            return null;
        } catch (SecurityException | MalformedJwtException exception) {
            LOGGER.info("잘못된 JWT 서명");
            return null;
        } catch (RuntimeException exception) {
            LOGGER.info("valid token error");
            return null;
        }
    }

    @LogExecutionTime
    public String getAudience(String token) {
        return parseClaims(token).getAudience();
    }

    public boolean checkExpire(Claims claims) {
        try {
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            LOGGER.info("만료된 JWT 토큰");
            return false;
        }
    }

    public boolean isValidatedToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey.value())
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            LOGGER.info("만료된 JWT 토큰");
            return false;
        } catch (IllegalArgumentException exception) {
            LOGGER.info("잘못된 JWT 토큰");
            return false;
        } catch (SecurityException | MalformedJwtException e) {
            LOGGER.info("잘못된 JWT 서명");
            return false;
        } catch (RuntimeException exception) {
            LOGGER.info("valid token error");
            return false;
        }
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader(TokenConstants.AUTHORIZATION_HEADER);
        if (authorization == null) {
            throw new BusinessException(ErrorCode.EMPTY_TOKEN_PROVIDED, HttpStatus.UNAUTHORIZED);
        }
        if (authorization.startsWith(TokenConstants.BEARER_PREFIX)) {
            return authorization.substring(TokenConstants.BEARER_PREFIX.length());
        }
        return authorization;
    }

    private Claims parseClaims(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey.value())
                    .build()
                    .parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                throw new BusinessException(ErrorCode.EXPIRED_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED);
            }
            return claims.getBody();
        } catch (ExpiredJwtException exception) {
            throw new BusinessException(ErrorCode.EXPIRED_ACCESS_TOKEN, HttpStatus.UNAUTHORIZED);
        } catch (SecurityException | MalformedJwtException exception) {
            throw new BusinessException(ErrorCode.TAMPERED_TOKEN_SIGNATURE, HttpStatus.UNAUTHORIZED);
        } catch (BusinessException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }
}
