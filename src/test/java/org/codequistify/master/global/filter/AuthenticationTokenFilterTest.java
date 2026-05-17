package org.codequistify.master.global.filter;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.jwt.TokenIssuer;
import org.codequistify.master.global.jwt.TokenSigningKey;
import org.codequistify.master.global.jwt.TokenVerifier;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.TokenAudience;
import org.codequistify.master.global.jwt.vo.TokenAuthorities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class AuthenticationTokenFilterTest {

    @AfterEach
    void 인증_컨텍스트를_정리한다() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void AccessToken_인증은_DB_조회_없이_Principal을_설정한다() throws ServletException, IOException {
        TokenSigningKey signingKey = signingKey();
        TokenIssuer tokenIssuer = new TokenIssuer(signingKey);
        TokenVerifier tokenVerifier = new TokenVerifier(signingKey);
        AccessToken token = tokenIssuer.issueAccessToken(new AccessTokenClaims(
                TokenAudience.from("player-id"),
                TokenAuthorities.fromStoredAuthorities(List.of(
                        PlayerRoleType.PLAYER.getRole(), PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission()))));
        AuthenticationTokenFilter filter = new AuthenticationTokenFilter(tokenVerifier);
        ReflectionTestUtils.setField(filter, "antMatchURIs", List.of("/api/auth"));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/players/me/stages");
        request.addHeader("Authorization", "Bearer " + token.value());
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication.getPrincipal()).isInstanceOf(PlayerPrincipal.class);
        PlayerPrincipal principal = (PlayerPrincipal) authentication.getPrincipal();
        assertThat(principal.id().value()).isEqualTo("player-id");
        assertThat(principal.roles().contains(PlayerRoleType.PLAYER)).isTrue();
    }

    private TokenSigningKey signingKey() {
        TokenSigningKey signingKey = new TokenSigningKey();
        ReflectionTestUtils.setField(signingKey, "jwtSecret", "12345678901234567890123456789012");
        signingKey.init();
        return signingKey;
    }
}
