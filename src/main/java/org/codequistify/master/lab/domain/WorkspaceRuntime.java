package org.codequistify.master.domain.lab.domain;

import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.domain.vo.WorkspaceRuntimeName;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;

public record WorkspaceRuntime(WorkspaceRuntimeName name, VirtualWorkspaceStage stage, PlayerId playerId) {
    public WorkspaceRuntime {
        if (name == null || stage == null || playerId == null) {
            throw new IllegalArgumentException("workspace runtime fields must not be null");
        }
    }

    public static WorkspaceRuntime from(VirtualWorkspaceStage stage, PlayerId playerId) {
        return new WorkspaceRuntime(WorkspaceRuntimeName.from(stage.image(), playerId), stage, playerId);
    }

    public StageImageType image() {
        return stage.image();
    }

    public Long stageId() {
        return stage.id();
    }
}
