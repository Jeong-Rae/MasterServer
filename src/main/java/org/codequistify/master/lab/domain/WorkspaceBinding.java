package org.codequistify.master.domain.lab.domain;

import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.domain.vo.WorkspaceBindingName;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;

public record WorkspaceBinding(WorkspaceBindingName name, VirtualWorkspaceStage stage, PlayerId playerId) {
    public WorkspaceBinding {
        if (name == null || stage == null || playerId == null) {
            throw new IllegalArgumentException("workspace binding fields must not be null");
        }
    }

    public static WorkspaceBinding from(VirtualWorkspaceStage stage, PlayerId playerId) {
        return new WorkspaceBinding(WorkspaceBindingName.from(stage.image(), playerId), stage, playerId);
    }

    public StageImageType image() {
        return stage.image();
    }

    public String stageId() {
        return stage.id();
    }
}
