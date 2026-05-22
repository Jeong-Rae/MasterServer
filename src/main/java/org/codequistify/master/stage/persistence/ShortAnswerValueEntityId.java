package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ShortAnswerValueEntityId implements Serializable {
    @Convert(converter = UuidStringBinaryConverter.class)
    @Column(name = "stage_id", columnDefinition = "BINARY(16)", nullable = false)
    private String stageId;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    @Column(name = "answer_index", nullable = false)
    private int answerIndex;

    protected ShortAnswerValueEntityId() {}

    public ShortAnswerValueEntityId(String stageId, int questionIndex, int answerIndex) {
        this.stageId = stageId;
        this.questionIndex = questionIndex;
        this.answerIndex = answerIndex;
    }

    public String stageId() {
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
