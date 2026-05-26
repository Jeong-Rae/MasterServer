package org.codequistify.master.stage.dto;

import jakarta.validation.constraints.NotNull;

public record QuestionCommandRequest(
        @NotNull(message = "4101") QuestionCommandType command, String submittedAnswer) {}
