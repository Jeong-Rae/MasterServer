package org.codequistify.master.domain.lab.domain.vo;

import java.util.Locale;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;

public record WorkspaceBindingName(String value) {
    public WorkspaceBindingName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("workspace binding name must not be blank");
        }
    }

    public static WorkspaceBindingName from(StageImageType stageImage, PlayerId playerId) {
        if (stageImage == null || playerId == null) {
            throw new IllegalArgumentException("workspace binding name fields must not be null");
        }
        return new WorkspaceBindingName(
                stageImage.resourceCode() + "-" + playerId.value().toLowerCase(Locale.ROOT));
    }
}
