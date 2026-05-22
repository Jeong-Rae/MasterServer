package org.codequistify.master.stage.domain.answer;

import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.vo.QuestionAnswer;
import org.codequistify.master.stage.domain.vo.QuestionOptions;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;

public final class MultipleChoiceAnswer implements Answer, MatchableAnswer, OptionSelectable {
    private final QuestionAnswer correctAnswer;
    private final QuestionOptions options;

    public MultipleChoiceAnswer(QuestionAnswer correctAnswer, QuestionOptions options) {
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public MultipleChoiceAnswer(String correctAnswer, QuestionOptions options) {
        this(QuestionAnswer.of(correctAnswer == null ? "" : correctAnswer), options);
    }

    @Override
    public AnswerType type() {
        return AnswerType.MULTIPLE_CHOICE;
    }

    @Override
    public boolean matches(SubmittedAnswer submittedAnswer) {
        return correctAnswer.matches(submittedAnswer);
    }

    @Override
    public QuestionAnswer correctAnswer() {
        return correctAnswer;
    }

    @Override
    public QuestionOptions options() {
        return options;
    }
}
