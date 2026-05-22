package org.codequistify.master.stage.domain;

import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;

public class PlayerStageSolving {
    private final PlayerId playerId;
    private final StageId stageId;
    private SolvingStatus status;
    private QuestionIndex currentQuestionIndex;

    private PlayerStageSolving(
            PlayerId playerId, StageId stageId, SolvingStatus status, QuestionIndex currentQuestionIndex) {
        if (playerId == null || stageId == null || status == null || currentQuestionIndex == null) {
            throw new IllegalArgumentException("player stage solving fields must not be null");
        }
        this.playerId = playerId;
        this.stageId = stageId;
        this.status = status;
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public static PlayerStageSolving start(PlayerId playerId, StageId stageId) {
        return new PlayerStageSolving(playerId, stageId, SolvingStatus.IN_PROGRESS, QuestionIndex.FIRST);
    }

    public PlayerStageSolving progressTo(QuestionIndex questionIndex) {
        if (status != SolvingStatus.IN_PROGRESS) {
            throw new IllegalStateException("only in-progress solving can progress");
        }
        currentQuestionIndex = questionIndex;
        return this;
    }

    public PlayerStageSolving completeAt(QuestionIndex questionIndex) {
        progressTo(questionIndex);
        status = SolvingStatus.COMPLETED;
        return this;
    }

    public PlayerStageSolving abandon() {
        if (status == SolvingStatus.COMPLETED) {
            throw new IllegalStateException("completed solving cannot be abandoned");
        }
        status = SolvingStatus.ABANDONED;
        return this;
    }

    public boolean isCompleted() {
        return status == SolvingStatus.COMPLETED;
    }

    public PlayerId playerId() {
        return playerId;
    }

    public StageId stageId() {
        return stageId;
    }

    public SolvingStatus status() {
        return status;
    }

    public QuestionIndex currentQuestionIndex() {
        return currentQuestionIndex;
    }
}
