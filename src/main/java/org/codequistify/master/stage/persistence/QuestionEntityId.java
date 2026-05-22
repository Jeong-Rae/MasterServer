package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionEntityId implements Serializable {
    @Convert(converter = UuidStringBinaryConverter.class)
    @Column(name = "stage_id", columnDefinition = "BINARY(16)", nullable = false)
    private String stageId;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    protected QuestionEntityId() {}

    public QuestionEntityId(String stageId, int questionIndex) {
        this.stageId = stageId;
        this.questionIndex = questionIndex;
    }

    public String stageId() {
        return stageId;
    }

    public int questionIndex() {
        return questionIndex;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof QuestionEntityId that)) {
            return false;
        }
        return questionIndex == that.questionIndex && Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageId, questionIndex);
    }
}
