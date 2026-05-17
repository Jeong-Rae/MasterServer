package org.codequistify.master.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.jwt.TokenVerifier;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private final TokenVerifier tokenVerifier;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

    @Value("${host.develop.api.ant-match.uri}")
    private List<String> antMatchURIs = new ArrayList<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // 토큰 비검사 uri
        for (String antMatchURI : antMatchURIs) {
            if (path.startsWith(antMatchURI)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String token = tokenVerifier.resolveToken(request);
        AccessTokenClaims claims = tokenVerifier.parseAccessToken(token);
        PlayerPrincipal principal = claims.toPrincipal();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        LOGGER.info("[AuthenticationTokenFilter] {}", authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
