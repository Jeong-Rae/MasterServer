package org.codequistify.master.domain.lab.domain.vo;

import java.util.Locale;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;

public record WorkspaceRuntimeName(String value) {
    public WorkspaceRuntimeName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("workspace runtime name must not be blank");
        }
    }

    public static WorkspaceRuntimeName from(StageImageType stageImage, PlayerId playerId) {
        if (stageImage == null || playerId == null) {
            throw new IllegalArgumentException("workspace runtime name fields must not be null");
        }
        return new WorkspaceRuntimeName(
                stageImage.resourceCode() + "-" + playerId.value().toLowerCase(Locale.ROOT));
    }
}
