package org.codequistify.master.stage.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.codequistify.master.global.util.BaseTimeEntity;

@Entity
@Table(name = "question")
public class QuestionEntity extends BaseTimeEntity {
    @EmbeddedId
    private QuestionEntityId id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @MapsId("stageId")
    @ManyToOne
    @JoinColumn(name = "stage_id", nullable = false)
    private StageEntity stage;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private AnswerEntity answer;

    protected QuestionEntity() {}

    private QuestionEntity(QuestionEntityId id, String title, String description, AnswerEntity answer) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.answer = answer;
        this.answer.attachQuestion(this);
    }

    public static QuestionEntity create(QuestionEntityId id, String title, String description, AnswerEntity answer) {
        return new QuestionEntity(id, title, description, answer);
    }

    void attachStage(StageEntity stage) {
        this.stage = stage;
    }

    public QuestionEntityId id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public StageEntity stage() {
        return stage;
    }

    public AnswerEntity answer() {
        return answer;
    }
}
