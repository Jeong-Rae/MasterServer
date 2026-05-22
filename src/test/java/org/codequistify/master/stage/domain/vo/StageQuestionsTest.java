package org.codequistify.master.stage.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.codequistify.master.stage.domain.Question;
import org.junit.jupiter.api.Test;

class StageQuestionsTest {

    @Test
    void StageQuestions는_문항_count와_순서_index를_관리한다() {
        Question first = Question.builder().title("1").description("1").build();
        Question second = Question.builder().title("2").description("2").build();
        StageQuestions questions = StageQuestions.of(List.of(first, second));

        questions.assignIndexes();

        assertThat(questions.count()).isEqualTo(2);
        assertThat(first.index().value()).isEqualTo(1);
        assertThat(second.index().value()).isEqualTo(2);
    }

    @Test
    void StageQuestions는_마지막_문항과_응답용_next_index를_계산한다() {
        Question first = Question.builder().title("1").description("1").build();
        Question second = Question.builder().title("2").description("2").build();
        StageQuestions questions = StageQuestions.of(List.of(first, second)).assignIndexes();

        assertThat(questions.isLast(new QuestionIndex(1))).isFalse();
        assertThat(questions.nextResponseIndexAfter(new QuestionIndex(1))).isEqualTo(2);
        assertThat(questions.isLast(new QuestionIndex(2))).isTrue();
        assertThat(questions.nextResponseIndexAfter(new QuestionIndex(2))).isEqualTo(-1);
    }

    @Test
    void StageQuestions는_StageId와_QuestionIndex로_QuestionId를_부여한다() {
        Question first = Question.builder().title("1").description("1").build();
        Question second = Question.builder().title("2").description("2").build();
        StageQuestions questions = StageQuestions.of(List.of(first, second)).assignIndexes();

        questions.assignIds(new StageId("10"));

        assertThat(first.id().stageId().value()).isEqualTo("10");
        assertThat(first.id().index().value()).isEqualTo(1);
        assertThat(second.id().stageId().value()).isEqualTo("10");
        assertThat(second.id().index().value()).isEqualTo(2);
    }
}
