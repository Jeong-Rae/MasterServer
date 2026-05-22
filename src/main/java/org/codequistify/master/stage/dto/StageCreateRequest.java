package org.codequistify.master.stage.dto;

import java.util.List;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.StageGroupType;

public record StageCreateRequest(
        String title,
        String description,
        StageGroupType stageGroup,
        DifficultyLevelType difficultyLevel,
        List<StageQuestionCreateRequest> questions) {}
