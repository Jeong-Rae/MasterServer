package org.codequistify.master.stage.dto;

import java.util.List;

public record StagePageResponse(List<StageResponse> stages, PageMetadataResponse pageMetadata) {
    public static StagePageResponse of(List<StageResponse> stages, PageMetadataResponse pageMetadata) {
        return new StagePageResponse(stages, pageMetadata);
    }
}
