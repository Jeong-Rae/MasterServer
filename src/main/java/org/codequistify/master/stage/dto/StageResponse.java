package org.codequistify.master.stage.dto;

import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageGroupType;

public record StageResponse(
        String id,
        String stageCode,
        String title,
        String description,
        StageGroupType stageGroup,
        DifficultyLevelType difficultyLevel,
        int questionCount,
        CompletedStatus completedStatus) {

    public static StageResponse from(Stage stage, CompletedStatus status) {
        return new StageResponse(
                stage.id().value(),
                stage.stageCode(),
                stage.title(),
                stage.description(),
                stage.stageGroup(),
                stage.difficultyLevel(),
                stage.stageQuestions().count(),
                status);
    }
}
