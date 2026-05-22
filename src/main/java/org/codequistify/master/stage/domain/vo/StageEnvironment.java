package org.codequistify.master.stage.domain.vo;

import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageImageType;

public record StageEnvironment(StageId stageId, StageImageType stageImage) {
    public StageEnvironment {
        if (stageId == null || stageImage == null) {
            throw new IllegalArgumentException("stage environment fields must not be null");
        }
    }

    public static StageEnvironment from(Stage stage) {
        if (stage == null) {
            throw new IllegalArgumentException("stage must not be null");
        }
        return new StageEnvironment(stage.id(), stage.stageImage());
    }

    public String stageCode() {
        return stageImage.code();
    }

    public String resourceCode() {
        return stageImage.resourceCode();
    }
}
