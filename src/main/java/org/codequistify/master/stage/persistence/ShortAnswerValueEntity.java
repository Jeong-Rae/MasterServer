package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "short_answer_value")
public class ShortAnswerValueEntity {
    @EmbeddedId
    private ShortAnswerValueEntityId id;

    @Column(name = "answer_text", nullable = false, length = 500)
    private String answerText;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "stage_id", referencedColumnName = "stage_id", insertable = false, updatable = false),
        @JoinColumn(
                name = "question_index",
                referencedColumnName = "question_index",
                insertable = false,
                updatable = false)
    })
    private AnswerEntity answer;

    protected ShortAnswerValueEntity() {}

    private ShortAnswerValueEntity(ShortAnswerValueEntityId id, String answerText) {
        this.id = id;
        this.answerText = answerText;
    }

    public static ShortAnswerValueEntity create(ShortAnswerValueEntityId id, String answerText) {
        return new ShortAnswerValueEntity(id, answerText);
    }

    void attachAnswer(AnswerEntity answer) {
        this.answer = answer;
    }

    public ShortAnswerValueEntityId id() {
        return id;
    }

    public String answerText() {
        return answerText;
    }
}
