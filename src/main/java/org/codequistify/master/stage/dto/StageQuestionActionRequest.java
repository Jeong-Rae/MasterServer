package org.codequistify.master.stage.dto;

import java.util.Locale;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageEnvironment;

public record StageQuestionActionRequest(String stageCode, int questionIndex) {
    public StageQuestionActionRequest {
        if (stageCode == null || stageCode.isBlank()) {
            throw new IllegalArgumentException("stage code must not be blank");
        }
        stageCode = stageCode.toLowerCase(Locale.ROOT);
    }

    public static StageQuestionActionRequest from(StageEnvironment environment, QuestionIndex questionIndex) {
        return new StageQuestionActionRequest(environment.resourceCode(), questionIndex.value());
    }
}
