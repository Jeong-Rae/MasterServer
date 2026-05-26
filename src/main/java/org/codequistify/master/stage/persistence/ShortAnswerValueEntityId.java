package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ShortAnswerValueEntityId implements Serializable {
    @Column(name = "stage_id", nullable = false)
    private UUID stageId;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    @Column(name = "answer_index", nullable = false)
    private int answerIndex;

    protected ShortAnswerValueEntityId() {}

    public ShortAnswerValueEntityId(UUID stageId, int questionIndex, int answerIndex) {
        this.stageId = stageId;
        this.questionIndex = questionIndex;
        this.answerIndex = answerIndex;
    }

    public UUID stageId() {
        return stageId;
    }

    public int questionIndex() {
        return questionIndex;
    }

    public int answerIndex() {
        return answerIndex;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ShortAnswerValueEntityId that)) {
            return false;
        }
        return questionIndex == that.questionIndex
                && answerIndex == that.answerIndex
                && Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageId, questionIndex, answerIndex);
    }
}
