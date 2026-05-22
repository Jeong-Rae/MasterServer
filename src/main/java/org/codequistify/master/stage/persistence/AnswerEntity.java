package org.codequistify.master.stage.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.codequistify.master.global.util.BaseTimeEntity;
import org.codequistify.master.stage.domain.AnswerType;

@Entity
@Table(name = "answer")
public class AnswerEntity extends BaseTimeEntity {
    @EmbeddedId
    private QuestionEntityId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type", nullable = false, length = 30)
    private AnswerType answerType;

    @Column(name = "match_policy", length = 30)
    private String matchPolicy;

    @Column(name = "requires_compose", nullable = false)
    private boolean requiresCompose;

    @MapsId
    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "stage_id", referencedColumnName = "stage_id"),
        @JoinColumn(name = "question_index", referencedColumnName = "question_index")
    })
    private QuestionEntity question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerOptionEntity> options = new ArrayList<>();

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortAnswerValueEntity> shortAnswerValues = new ArrayList<>();

    protected AnswerEntity() {}

    private AnswerEntity(QuestionEntityId id, AnswerType answerType, String matchPolicy, boolean requiresCompose) {
        this.id = id;
        this.answerType = answerType;
        this.matchPolicy = matchPolicy;
        this.requiresCompose = requiresCompose;
    }

    public static AnswerEntity multipleChoice(QuestionEntityId id, List<AnswerOptionEntity> options) {
        AnswerEntity answer = new AnswerEntity(id, AnswerType.MULTIPLE_CHOICE, "EXACT", false);
        options.forEach(answer::addOption);
        return answer;
    }

    public static AnswerEntity shortAnswer(QuestionEntityId id, List<ShortAnswerValueEntity> values) {
        AnswerEntity answer = new AnswerEntity(id, AnswerType.SHORT_ANSWER, "EXACT", false);
        values.forEach(answer::addShortAnswerValue);
        return answer;
    }

    public static AnswerEntity practical(QuestionEntityId id, boolean requiresCompose) {
        return new AnswerEntity(id, AnswerType.PRACTICAL, null, requiresCompose);
    }

    public static AnswerEntity description(QuestionEntityId id) {
        return new AnswerEntity(id, AnswerType.DESCRIPTION, null, false);
    }

    void attachQuestion(QuestionEntity question) {
        this.question = question;
    }

    private void addOption(AnswerOptionEntity option) {
        option.attachAnswer(this);
        options.add(option);
    }

    private void addShortAnswerValue(ShortAnswerValueEntity value) {
        value.attachAnswer(this);
        shortAnswerValues.add(value);
    }

    public QuestionEntityId id() {
        return id;
    }

    public AnswerType answerType() {
        return answerType;
    }

    public String matchPolicy() {
        return matchPolicy;
    }

    public boolean requiresCompose() {
        return requiresCompose;
    }

    public List<AnswerOptionEntity> options() {
        return List.copyOf(options);
    }

    public List<ShortAnswerValueEntity> shortAnswerValues() {
        return List.copyOf(shortAnswerValues);
    }
}
