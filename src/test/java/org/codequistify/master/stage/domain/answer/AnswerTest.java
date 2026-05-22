package org.codequistify.master.stage.domain.answer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.vo.Option;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;
import org.junit.jupiter.api.Test;

class AnswerTest {

    @Test
    void 선택형_답안은_선택지와_정답을_가지고_제출값을_채점한다() {
        Answer answer = Answer.of(AnswerType.MULTIPLE_CHOICE, "A", List.of("A", "B"), false);

        assertThat(answer.type()).isEqualTo(AnswerType.MULTIPLE_CHOICE);
        assertThat(answer).isInstanceOf(MatchableAnswer.class);
        assertThat(answer).isInstanceOf(OptionSelectable.class);

        OptionSelectable optionSelectable = (OptionSelectable) answer;
        MatchableAnswer matchableAnswer = (MatchableAnswer) answer;
        assertThat(optionSelectable.options().values())
                .extracting(Option::value)
                .containsExactly("A", "B");
        assertThat(optionSelectable.options().rawValues()).containsExactly("A", "B");
        assertThat(matchableAnswer.matches(SubmittedAnswer.of("a"))).isTrue();
    }

    @Test
    void 실행형_답안은_채점_비교_대상이_아니며_실행_필요여부만_가진다() {
        Answer answer = Answer.of(AnswerType.PRACTICAL, "", List.of(), true);

        assertThat(answer.type()).isEqualTo(AnswerType.PRACTICAL);
        assertThat(answer).isInstanceOf(ExecutionRequired.class);
        assertThat(answer).isNotInstanceOf(MatchableAnswer.class);
        assertThat(((ExecutionRequired) answer).requiresCompose()).isTrue();
    }

    @Test
    void 설명형은_Description_타입이며_제출값과_무관하게_항상_정답이다() {
        Answer answer = Answer.of(AnswerType.DESCRIPTION, "ignored", List.of(), false);

        assertThat(answer).isInstanceOf(Description.class);
        assertThat(answer).isInstanceOf(AlwaysCorrectAnswer.class);
        assertThat(answer).isNotInstanceOf(MatchableAnswer.class);
        assertThat(answer.type()).isEqualTo(AnswerType.DESCRIPTION);
        assertThat(((AlwaysCorrectAnswer) answer).isCorrect()).isTrue();
    }
}
