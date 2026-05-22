package org.codequistify.master.stage.domain.answer;

import org.codequistify.master.stage.domain.AnswerType;

public final class PracticalAnswer implements Answer, ExecutionRequired {
    private final boolean requiresCompose;

    public PracticalAnswer(boolean requiresCompose) {
        this.requiresCompose = requiresCompose;
    }

    @Override
    public AnswerType type() {
        return AnswerType.PRACTICAL;
    }

    @Override
    public boolean requiresCompose() {
        return requiresCompose;
    }
}
