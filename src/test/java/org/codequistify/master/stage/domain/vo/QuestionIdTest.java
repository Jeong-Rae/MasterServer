package org.codequistify.master.stage.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class QuestionIdTest {

    @Test
    void QuestionId는_StageId와_QuestionIndex를_복합_식별자로_가진다() {
        QuestionId questionId = QuestionId.of(new StageId("10"), new QuestionIndex(2));

        assertThat(questionId.stageId().value()).isEqualTo("10");
        assertThat(questionId.index().value()).isEqualTo(2);
    }

    @Test
    void QuestionId는_StageId와_QuestionIndex가_모두_필요하다() {
        assertThatThrownBy(() -> QuestionId.of(null, QuestionIndex.FIRST)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> QuestionId.of(new StageId("1"), null)).isInstanceOf(IllegalArgumentException.class);
    }
}
