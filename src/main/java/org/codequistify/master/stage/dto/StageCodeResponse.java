package org.codequistify.master.stage.dto;

import org.codequistify.master.stage.domain.StageImageType;

public record StageCodeResponse(StageImageType stageCode) {
    public static StageCodeResponse of(StageImageType stageImageType) {
        return new StageCodeResponse(stageImageType);
    }
}
