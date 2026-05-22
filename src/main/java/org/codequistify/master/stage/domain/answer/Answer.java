package org.codequistify.master.stage.domain.answer;

import java.util.List;
import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.vo.QuestionOptions;

public sealed interface Answer permits MultipleChoiceAnswer, ShortAnswer, PracticalAnswer, Description {
    AnswerType type();

    static Answer of(AnswerType type, String correctAnswer, List<String> options, boolean requiresCompose) {
        AnswerType answerType = type == null ? AnswerType.SHORT_ANSWER : type;
        QuestionOptions questionOptions = QuestionOptions.of(options);

        return switch (answerType) {
            case MULTIPLE_CHOICE -> new MultipleChoiceAnswer(correctAnswer, questionOptions);
            case SHORT_ANSWER -> new ShortAnswer(correctAnswer);
            case PRACTICAL -> new PracticalAnswer(requiresCompose);
            case DESCRIPTION -> new Description();
        };
    }
}
