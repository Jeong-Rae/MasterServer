package org.codequistify.master.stage.domain.answer;

import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.vo.QuestionAnswer;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;

public final class ShortAnswer implements Answer, MatchableAnswer {
    private final QuestionAnswer correctAnswer;

    public ShortAnswer(QuestionAnswer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public ShortAnswer(String correctAnswer) {
        this(QuestionAnswer.of(correctAnswer == null ? "" : correctAnswer));
    }

    @Override
    public AnswerType type() {
        return AnswerType.SHORT_ANSWER;
    }

    @Override
    public boolean matches(SubmittedAnswer submittedAnswer) {
        return correctAnswer.matches(submittedAnswer);
    }

    @Override
    public QuestionAnswer correctAnswer() {
        return correctAnswer;
    }
}
