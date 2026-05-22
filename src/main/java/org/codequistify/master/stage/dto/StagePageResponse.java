package org.codequistify.master.stage.dto;

import java.util.List;

public record StagePageResponse(List<StageResponse> stages, PageMetadata pageMetadata) {
    public static StagePageResponse of(List<StageResponse> stages, PageMetadata pageMetadata) {
        return new StagePageResponse(stages, pageMetadata);
    }
}
