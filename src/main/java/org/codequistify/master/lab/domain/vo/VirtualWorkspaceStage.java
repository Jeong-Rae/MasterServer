package org.codequistify.master.domain.lab.domain.vo;

import org.codequistify.master.domain.stage.domain.Stage;
import org.codequistify.master.domain.stage.domain.StageImageType;

public record VirtualWorkspaceStage(Long id, StageImageType image) {
    public VirtualWorkspaceStage {
        if (id == null) {
            throw new IllegalArgumentException("virtual workspace stage id must not be null");
        }
        if (image == null) {
            throw new IllegalArgumentException("virtual workspace stage image must not be null");
        }
    }

    public static VirtualWorkspaceStage from(Stage stage) {
        return new VirtualWorkspaceStage(stage.getId(), stage.getStageImage());
    }
}
