package org.codequistify.master.stage.dto;

public record QuestionGradingResponse(boolean correct, int nextQuestionIndex, boolean last, boolean composable) {}
