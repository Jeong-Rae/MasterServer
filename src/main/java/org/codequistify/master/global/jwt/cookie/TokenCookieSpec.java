package org.codequistify.master.global.jwt.cookie;

import jakarta.servlet.http.Cookie;
import org.codequistify.master.global.jwt.vo.IssuedTokens;

enum TokenCookieSpec {
    SERVICE_REFRESH(
            "POL_REFRESH_TOKEN", "pol.or.kr", TokenCookieMaxAge.REFRESH_SECONDS, true, true, TokenCookieType.REFRESH),
    SERVICE_ACCESS(
            "POL_ACCESS_TOKEN", "pol.or.kr", TokenCookieMaxAge.ACCESS_SECONDS, true, true, TokenCookieType.ACCESS),
    DEVELOPMENT_REFRESH(
            "POL_REFRESH_TOKEN_DEV",
            "localhost",
            TokenCookieMaxAge.REFRESH_SECONDS,
            false,
            false,
            TokenCookieType.REFRESH),
    DEVELOPMENT_ACCESS(
            "POL_ACCESS_TOKEN_DEV",
            "localhost",
            TokenCookieMaxAge.ACCESS_SECONDS,
            false,
            false,
            TokenCookieType.ACCESS);

    private final String name;
    private final String domain;
    private final int maxAge;
    private final boolean httpOnly;
    private final boolean secure;
    private final TokenCookieType type;

    TokenCookieSpec(String name, String domain, int maxAge, boolean httpOnly, boolean secure, TokenCookieType type) {
        this.name = name;
        this.domain = domain;
        this.maxAge = maxAge;
        this.httpOnly = httpOnly;
        this.secure = secure;
        this.type = type;
    }

    Cookie create(IssuedTokens tokens) {
        return create(type.value(tokens));
    }

    Cookie create(String value) {
        return create(value, maxAge);
    }

    Cookie expire() {
        return create(null, 0);
    }

    private Cookie create(String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        return cookie;
    }

    private static final class TokenCookieMaxAge {
        private static final int ACCESS_SECONDS = 60 * 60;
        private static final int REFRESH_SECONDS = 7 * 24 * 60 * 60;

        private TokenCookieMaxAge() {}
    }

    private enum TokenCookieType {
        REFRESH {
            @Override
            String value(IssuedTokens tokens) {
                return tokens.refreshToken().value();
            }
        },
        ACCESS {
            @Override
            String value(IssuedTokens tokens) {
                return tokens.accessToken().value();
            }
        };

        abstract String value(IssuedTokens tokens);
    }
}
