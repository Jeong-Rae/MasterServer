package org.codequistify.master.stage.domain.vo;

import java.util.List;
import org.codequistify.master.stage.domain.Question;

public record StageQuestions(List<Question> values) {
    public StageQuestions {
        if (values == null) {
            throw new IllegalArgumentException("stage questions must not be null");
        }
        values = List.copyOf(values);
    }

    public static StageQuestions of(List<Question> values) {
        return new StageQuestions(values);
    }

    public static StageQuestions empty() {
        return new StageQuestions(List.of());
    }

    public StageQuestions assignIndexes() {
        for (int index = 0; index < values.size(); index++) {
            values.get(index).assignIndex(new QuestionIndex(index + 1));
        }
        return this;
    }

    public StageQuestions assignIds(StageId stageId) {
        for (Question question : values) {
            question.assignId(QuestionId.of(stageId, question.index()));
        }
        return this;
    }

    public int count() {
        return values.size();
    }

    public boolean isLast(QuestionIndex index) {
        return index.value() >= values.size();
    }

    public int nextResponseIndexAfter(QuestionIndex index) {
        if (isLast(index)) {
            return -1;
        }
        return index.next().value();
    }

    public boolean nextQuestionRequiresCompose(QuestionIndex index) {
        if (isLast(index)) {
            return false;
        }
        return values.get(index.value() - 1).requiresCompose();
    }
}
