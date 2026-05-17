package org.codequistify.master.global.jwt.cookie;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.jwt.vo.IssuedTokens;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCookieManager {
    private final Environment environment;

    public void addTokenCookies(HttpServletResponse response, IssuedTokens tokens) {
        cookieSpecs().forEach(spec -> response.addCookie(spec.create(tokens)));
    }

    public void expireTokenCookies(HttpServletResponse response) {
        cookieSpecs().forEach(spec -> response.addCookie(spec.expire()));
    }

    private List<TokenCookieSpec> cookieSpecs() {
        if (isProductionProfile()) {
            return List.of(TokenCookieSpec.SERVICE_REFRESH, TokenCookieSpec.SERVICE_ACCESS);
        }
        return List.of(TokenCookieSpec.DEVELOPMENT_REFRESH, TokenCookieSpec.DEVELOPMENT_ACCESS);
    }

    private boolean isProductionProfile() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equals("prod") || profile.equals("production"));
    }
}
