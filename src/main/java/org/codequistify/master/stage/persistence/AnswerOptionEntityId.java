package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AnswerOptionEntityId implements Serializable {
    @Convert(converter = UuidStringBinaryConverter.class)
    @Column(name = "stage_id", columnDefinition = "BINARY(16)", nullable = false)
    private String stageId;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    @Column(name = "option_index", nullable = false)
    private int optionIndex;

    protected AnswerOptionEntityId() {}

    public AnswerOptionEntityId(String stageId, int questionIndex, int optionIndex) {
        this.stageId = stageId;
        this.questionIndex = questionIndex;
        this.optionIndex = optionIndex;
    }

    public String stageId() {
        return stageId;
    }

    public int questionIndex() {
        return questionIndex;
    }

    public int optionIndex() {
        return optionIndex;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AnswerOptionEntityId that)) {
            return false;
        }
        return questionIndex == that.questionIndex
                && optionIndex == that.optionIndex
                && Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageId, questionIndex, optionIndex);
    }
}
