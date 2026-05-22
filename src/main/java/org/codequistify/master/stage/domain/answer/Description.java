package org.codequistify.master.stage.domain.answer;

import org.codequistify.master.stage.domain.AnswerType;

public final class Description implements Answer, AlwaysCorrectAnswer {
    @Override
    public AnswerType type() {
        return AnswerType.DESCRIPTION;
    }

    @Override
    public boolean isCorrect() {
        return true;
    }
}
