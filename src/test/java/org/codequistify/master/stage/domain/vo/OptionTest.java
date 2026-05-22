package org.codequistify.master.stage.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class OptionTest {

    @Test
    void Option은_선택지_텍스트를_도메인_값으로_감싼다() {
        Option option = Option.of("A");

        assertThat(option.value()).isEqualTo("A");
    }

    @Test
    void Option은_null_텍스트를_허용하지_않는다() {
        assertThatThrownBy(() -> Option.of(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
