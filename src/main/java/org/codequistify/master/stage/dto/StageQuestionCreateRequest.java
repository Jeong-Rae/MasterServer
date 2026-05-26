package org.codequistify.master.stage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.codequistify.master.stage.domain.AnswerType;

public record StageQuestionCreateRequest(
        @NotBlank(message = "4101") String title,
        @NotBlank(message = "4101") String description,
        @NotNull(message = "4101") AnswerType answerType,
        String correctAnswer,
        List<@NotBlank(message = "4101") String> options) {}
