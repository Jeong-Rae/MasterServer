package org.codequistify.master.stage.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.StageGroupType;

public record StageCreateRequest(
        @NotBlank(message = "4101") String title,
        @NotBlank(message = "4101") String description,
        @NotNull(message = "4101") StageGroupType stageGroup,
        @NotNull(message = "4101") DifficultyLevelType difficultyLevel,
        @Valid @NotEmpty(message = "4101") List<StageQuestionCreateRequest> questions) {}
