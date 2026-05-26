package org.codequistify.master.stage.dto;

import java.time.LocalDate;

public record StageAttemptHeatMapPointResponse(LocalDate date, Long attemptCount) {}
