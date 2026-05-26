package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import org.codequistify.master.global.uuid.UUIDv7Generator;
import org.codequistify.master.stage.domain.AnswerType;

@Entity
@Table(
        name = "question_attempt",
        indexes = {
            @Index(
                    name = "idx_attempt_player_stage_question",
                    columnList = "player_id, stage_id, question_index, attempted_at"),
            @Index(name = "idx_attempt_stage_answer_type", columnList = "stage_id, answer_type, attempted_at"),
            @Index(name = "idx_attempt_player_attempted_at", columnList = "player_id, attempted_at")
        })
public class QuestionAttemptEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "stage_id", nullable = false)
    private UUID stageId;

    @Column(name = "question_index", nullable = false)
    private int questionIndex;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_type", nullable = false, length = 30)
    private AnswerType answerType;

    @Column(name = "selected_option_index")
    private Integer selectedOptionIndex;

    @Column(name = "submitted_text", length = 255)
    private String submittedText;

    @Column(name = "external_execution_id", length = 100)
    private String externalExecutionId;

    @Column(name = "result_code", length = 60)
    private String resultCode;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @Column(name = "attempted_at", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime attemptedAt;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime createdAt;

    protected QuestionAttemptEntity() {}

    private QuestionAttemptEntity(
            UUID playerId,
            UUID stageId,
            int questionIndex,
            AnswerType answerType,
            String submittedText,
            boolean correct) {
        this.id = UUID.fromString(UUIDv7Generator.generate());
        this.playerId = playerId;
        this.stageId = stageId;
        this.questionIndex = questionIndex;
        this.answerType = answerType;
        this.submittedText = submittedText;
        this.correct = correct;
        this.attemptedAt = LocalDateTime.now();
        this.createdAt = attemptedAt;
    }

    public static QuestionAttemptEntity create(
            UUID playerId,
            UUID stageId,
            int questionIndex,
            AnswerType answerType,
            String submittedText,
            boolean correct) {
        return new QuestionAttemptEntity(playerId, stageId, questionIndex, answerType, submittedText, correct);
    }
}
