package org.codequistify.master.global.jwt.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.IssuedTokens;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletResponse;

class TokenCookieManagerTest {
    @Test
    void production_profile은_Prod_쿠키만_삽입한다() {
        TokenCookieManager tokenCookieManager = tokenCookieManager("prod");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IssuedTokens tokens = new IssuedTokens(new RefreshToken("refresh-token"), new AccessToken("access-token"));

        tokenCookieManager.addTokenCookies(response, tokens);

        assertThat(response.getCookies()).extracting("name").containsExactly("POL_REFRESH_TOKEN", "POL_ACCESS_TOKEN");
        assertThat(response.getCookie("POL_REFRESH_TOKEN").getValue()).isEqualTo("refresh-token");
        assertThat(response.getCookie("POL_ACCESS_TOKEN").getValue()).isEqualTo("access-token");
    }

    @Test
    void production_이름의_profile도_Prod_쿠키만_삽입한다() {
        TokenCookieManager tokenCookieManager = tokenCookieManager("production");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IssuedTokens tokens = new IssuedTokens(new RefreshToken("refresh-token"), new AccessToken("access-token"));

        tokenCookieManager.addTokenCookies(response, tokens);

        assertThat(response.getCookies()).extracting("name").containsExactly("POL_REFRESH_TOKEN", "POL_ACCESS_TOKEN");
    }

    @Test
    void develop_profile은_Dev_쿠키만_삽입한다() {
        TokenCookieManager tokenCookieManager = tokenCookieManager("develop");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IssuedTokens tokens = new IssuedTokens(new RefreshToken("refresh-token"), new AccessToken("access-token"));

        tokenCookieManager.addTokenCookies(response, tokens);

        assertThat(response.getCookies())
                .extracting("name")
                .containsExactly("POL_REFRESH_TOKEN_DEV", "POL_ACCESS_TOKEN_DEV");
        assertThat(response.getCookie("POL_REFRESH_TOKEN_DEV").getValue()).isEqualTo("refresh-token");
        assertThat(response.getCookie("POL_ACCESS_TOKEN_DEV").getValue()).isEqualTo("access-token");
    }

    @Test
    void local_profile은_Dev_쿠키만_삽입한다() {
        TokenCookieManager tokenCookieManager = tokenCookieManager("local");
        MockHttpServletResponse response = new MockHttpServletResponse();
        IssuedTokens tokens = new IssuedTokens(new RefreshToken("refresh-token"), new AccessToken("access-token"));

        tokenCookieManager.addTokenCookies(response, tokens);

        assertThat(response.getCookies())
                .extracting("name")
                .containsExactly("POL_REFRESH_TOKEN_DEV", "POL_ACCESS_TOKEN_DEV");
    }

    @Test
    void profile에_맞는_토큰_쿠키만_만료한다() {
        TokenCookieManager tokenCookieManager = tokenCookieManager("develop");
        MockHttpServletResponse response = new MockHttpServletResponse();

        tokenCookieManager.expireTokenCookies(response);

        assertThat(response.getCookies())
                .extracting("name")
                .containsExactly("POL_REFRESH_TOKEN_DEV", "POL_ACCESS_TOKEN_DEV");
        assertThat(response.getCookies()).extracting("maxAge").containsExactly(0, 0);
    }

    @Test
    void 쿠키_정책은_enum이_값만_받아_Cookie를_생성한다() {
        assertThat(TokenCookieSpec.SERVICE_REFRESH.create("refresh-token").getName())
                .isEqualTo("POL_REFRESH_TOKEN");
        assertThat(TokenCookieSpec.SERVICE_REFRESH.create("refresh-token").getValue())
                .isEqualTo("refresh-token");
        assertThat(TokenCookieSpec.SERVICE_REFRESH.expire().getMaxAge()).isZero();
    }

    private TokenCookieManager tokenCookieManager(String profile) {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles(profile);
        return new TokenCookieManager(environment);
    }
}
