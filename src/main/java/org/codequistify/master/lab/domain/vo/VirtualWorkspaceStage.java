package org.codequistify.master.domain.lab.domain.vo;

import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageImageType;

public record VirtualWorkspaceStage(String id, StageImageType image) {
    public VirtualWorkspaceStage {
        if (id == null) {
            throw new IllegalArgumentException("virtual workspace stage id must not be null");
        }
        if (image == null) {
            throw new IllegalArgumentException("virtual workspace stage image must not be null");
        }
    }

    public static VirtualWorkspaceStage from(Stage stage) {
        return new VirtualWorkspaceStage(stage.id().value(), stage.stageImage());
    }
}
