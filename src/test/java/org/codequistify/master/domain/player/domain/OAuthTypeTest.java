package org.codequistify.master.domain.player.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.Test;

class OAuthTypeTest {

    @Test
    void 소문자_Provider_이름을_OAuthType으로_변환한다() {
        OAuthType result = OAuthType.from("google");

        assertThat(result).isEqualTo(OAuthType.GOOGLE);
    }

    @Test
    void 대문자_Provider_이름을_OAuthType으로_변환한다() {
        OAuthType result = OAuthType.from("KAKAO");

        assertThat(result).isEqualTo(OAuthType.KAKAO);
    }

    @Test
    void 지원하지_않는_Provider_이름이면_예외를_던진다() {
        assertThatThrownBy(() -> OAuthType.from("unknown")).isInstanceOf(BusinessException.class);
    }
}
