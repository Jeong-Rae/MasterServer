package org.codequistify.master.domain.player.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerNameTest {

    @Test
    void 한글_영문_숫자_이름을_생성하면_값을_보관한다() {
        PlayerName name = new PlayerName("플레이어A1");

        assertThat(name.value()).isEqualTo("플레이어A1");
    }

    @Test
    void 특수문자가_포함된_이름을_생성하면_예외를_던진다() {
        assertThatThrownBy(() -> new PlayerName("player!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 공백_이름을_생성하면_예외를_던진다() {
        assertThatThrownBy(() -> new PlayerName(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
