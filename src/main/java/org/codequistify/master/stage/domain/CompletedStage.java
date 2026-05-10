package org.codequistify.master.domain.stage.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.codequistify.master.global.util.BaseTimeEntity;

@ToString(exclude = "stage")
@Getter
@Entity
@Table(name = "completed_stage")
public class CompletedStage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", columnDefinition = "char(36)")
    private String playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    @JsonBackReference
    private Stage stage;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CompletedStatus status;

    @Column(name = "question_index", nullable = false)
    private Integer questionIndex = 0;

    public void updateQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
    }

    public void updateCompleted() {
        if (!status.equals(CompletedStatus.IN_PROGRESS)) {
            return;
        }
        this.status = CompletedStatus.COMPLETED;
    }

    @Builder
    public CompletedStage(String playerId, Stage stage, CompletedStatus status) {
        this.playerId = playerId;
        this.stage = stage;
        this.status = status;
    }

    protected CompletedStage() {
    }
}
