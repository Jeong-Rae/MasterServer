package org.codequistify.master.stage.dto;

import java.util.List;
import org.codequistify.master.stage.domain.AnswerType;

public record StageQuestionCreateRequest(
        String title, String description, AnswerType answerType, String correctAnswer, List<String> options) {}
