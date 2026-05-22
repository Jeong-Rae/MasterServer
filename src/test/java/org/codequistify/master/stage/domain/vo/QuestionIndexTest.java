package org.codequistify.master.stage.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class QuestionIndexTest {

    @Test
    void 첫번째_문항_index를_생성하고_next를_계산한다() {
        QuestionIndex index = QuestionIndex.FIRST;

        assertThat(index.value()).isEqualTo(1);
        assertThat(index.isFirst()).isTrue();
        assertThat(index.next().value()).isEqualTo(2);
    }

    @Test
    void 문항_index는_1보다_작으면_예외를_던진다() {
        assertThatThrownBy(() -> new QuestionIndex(0)).isInstanceOf(IllegalArgumentException.class);
    }
}
