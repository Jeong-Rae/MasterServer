package org.codequistify.master.stage.dto;

import java.util.List;
import org.codequistify.master.stage.domain.AnswerType;

public record StageQuestionResponse(
        String stageId,
        int questionIndex,
        String title,
        String description,
        AnswerType answerType,
        List<String> options) {}
