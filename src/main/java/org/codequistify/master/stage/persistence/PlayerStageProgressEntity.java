package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.codequistify.master.shared.persistence.BaseTimeEntity;
import org.codequistify.master.stage.domain.SolvingStatus;

@Entity
@Table(name = "player_stage_progress")
public class PlayerStageProgressEntity extends BaseTimeEntity {
    @EmbeddedId
    private PlayerStageProgressEntityId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SolvingStatus status;

    @Column(name = "current_question_index")
    private Integer currentQuestionIndex;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "last_solved_at")
    private LocalDateTime lastSolvedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected PlayerStageProgressEntity() {}

    private PlayerStageProgressEntity(
            PlayerStageProgressEntityId id,
            SolvingStatus status,
            Integer currentQuestionIndex,
            LocalDateTime startedAt,
            LocalDateTime lastSolvedAt,
            LocalDateTime completedAt) {
        this.id = id;
        this.status = status;
        this.currentQuestionIndex = currentQuestionIndex;
        this.startedAt = startedAt;
        this.lastSolvedAt = lastSolvedAt;
        this.completedAt = completedAt;
    }

    public static PlayerStageProgressEntity create(
            PlayerStageProgressEntityId id, SolvingStatus status, int currentQuestionIndex) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime completedAt = status == SolvingStatus.COMPLETED ? now : null;
        return new PlayerStageProgressEntity(id, status, currentQuestionIndex, now, now, completedAt);
    }

    public PlayerStageProgressEntityId id() {
        return id;
    }

    public SolvingStatus status() {
        return status;
    }

    public int currentQuestionIndex() {
        return currentQuestionIndex == null ? 0 : currentQuestionIndex;
    }
}
