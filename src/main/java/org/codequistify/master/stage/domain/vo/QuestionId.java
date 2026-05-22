package org.codequistify.master.stage.domain.vo;

public record QuestionId(StageId stageId, QuestionIndex index) {
    public QuestionId {
        if (stageId == null) {
            throw new IllegalArgumentException("question stage id must not be null");
        }
        if (index == null) {
            throw new IllegalArgumentException("question index must not be null");
        }
    }

    public static QuestionId of(StageId stageId, QuestionIndex index) {
        return new QuestionId(stageId, index);
    }
}
