package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer_option")
public class AnswerOptionEntity {
    @EmbeddedId
    private AnswerOptionEntityId id;

    @Column(name = "option_text", nullable = false, length = 500)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "correct_option_key", insertable = false, updatable = false)
    private Integer correctOptionKey;

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

    protected AnswerOptionEntity() {}

    private AnswerOptionEntity(AnswerOptionEntityId id, String optionText, boolean correct) {
        this.id = id;
        this.optionText = optionText;
        this.correct = correct;
    }

    public static AnswerOptionEntity create(AnswerOptionEntityId id, String optionText, boolean correct) {
        return new AnswerOptionEntity(id, optionText, correct);
    }

    void attachAnswer(AnswerEntity answer) {
        this.answer = answer;
    }

    public AnswerOptionEntityId id() {
        return id;
    }

    public String optionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return correct;
    }
}
