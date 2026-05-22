package org.codequistify.master.stage.dto;

import java.time.LocalDate;

public record StageAttemptHeatMapPoint(LocalDate date, Long attemptCount) {}
