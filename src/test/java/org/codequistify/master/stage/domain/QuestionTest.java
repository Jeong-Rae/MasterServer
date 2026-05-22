package org.codequistify.master.stage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.codequistify.master.stage.domain.answer.Answer;
import org.codequistify.master.stage.domain.answer.PracticalAnswer;
import org.codequistify.master.stage.domain.vo.QuestionId;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void Question은_Answer_객체에게_채점_책임을_위임한다() {
        Question question = Question.builder()
                .id(QuestionId.of(new StageId("1"), QuestionIndex.FIRST))
                .title("title")
                .description("description")
                .answer(Answer.of(AnswerType.SHORT_ANSWER, "polymorphism", List.of(), false))
                .build();

        assertThat(question.grade(SubmittedAnswer.of("Polymorphism"))).isTrue();
    }

    @Test
    void Question은_실행형_답안을_직접_비교하지_않는다() {
        Question question = Question.builder()
                .id(QuestionId.of(new StageId("1"), QuestionIndex.FIRST))
                .title("title")
                .description("description")
                .answer(new PracticalAnswer(true))
                .build();

        assertThat(question.grade(SubmittedAnswer.of("ignored"))).isFalse();
        assertThat(question.requiresCompose()).isTrue();
    }
}
