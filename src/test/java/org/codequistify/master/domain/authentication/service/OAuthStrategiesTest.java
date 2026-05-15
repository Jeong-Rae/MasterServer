package org.codequistify.master.domain.authentication.service;

import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.authentication.vo.OAuthData;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuthStrategiesTest {

    @Test
    void OAuthType으로_Strategy를_찾는다() {
        OAuthStrategy google = new StubOAuthStrategy(OAuthType.GOOGLE);
        OAuthStrategies strategies = new OAuthStrategies(List.of(google));

        OAuthStrategy result = strategies.find(OAuthType.GOOGLE);

        assertThat(result).isSameAs(google);
    }

    @Test
    void 등록되지_않은_OAuthType이면_예외를_던진다() {
        OAuthStrategies strategies = new OAuthStrategies(List.of(new StubOAuthStrategy(OAuthType.GOOGLE)));

        assertThatThrownBy(() -> strategies.find(OAuthType.KAKAO))
                .isInstanceOf(BusinessException.class);
    }

    private record StubOAuthStrategy(OAuthType type) implements OAuthStrategy {
        @Override
        public String loginUrl() {
            return "https://oauth.example/" + type.name().toLowerCase();
        }

        @Override
        public OAuthData fetchOAuthData(OAuthCode code) {
            throw new UnsupportedOperationException("not used");
        }
    }
}
