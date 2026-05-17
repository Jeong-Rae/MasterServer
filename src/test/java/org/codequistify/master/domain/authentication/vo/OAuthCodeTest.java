package org.codequistify.master.domain.authentication.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class OAuthCodeTest {

    @Test
    void OAuth_Code를_생성한다() {
        OAuthCode result = new OAuthCode("authorization-code");

        assertThat(result.value()).isEqualTo("authorization-code");
    }

    @Test
    void OAuth_Code가_비어있으면_예외를_던진다() {
        assertThatThrownBy(() -> new OAuthCode(" ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void OAuth_Code가_null이면_예외를_던진다() {
        assertThatThrownBy(() -> new OAuthCode(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
