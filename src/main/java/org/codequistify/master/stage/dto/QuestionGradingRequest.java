package org.codequistify.master.stage.dto;

import jakarta.validation.constraints.NotNull;

public record QuestionGradingRequest(
        @NotNull(message = "4101") String stageId,
        @NotNull(message = "4101") int questionIndex,
        String submittedAnswer) {}
