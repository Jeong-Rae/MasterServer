package org.codequistify.master.stage.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class QuestionAnswerTest {

    @Test
    void 정답과_제출답안은_대소문자를_무시하고_비교한다() {
        QuestionAnswer answer = new QuestionAnswer("Polymorphism");

        assertThat(answer.matches(new SubmittedAnswer("polymorphism"))).isTrue();
    }

    @Test
    void 제출답안이_null이면_일치하지_않는다() {
        QuestionAnswer answer = new QuestionAnswer("answer");

        assertThat(answer.matches(new SubmittedAnswer(null))).isFalse();
    }

    @Test
    void 정답이_null이면_예외를_던진다() {
        assertThatThrownBy(() -> new QuestionAnswer(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
