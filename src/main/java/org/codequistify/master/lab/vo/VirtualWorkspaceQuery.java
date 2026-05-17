package org.codequistify.master.domain.lab.vo;

import java.util.Locale;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;

public record VirtualWorkspaceQuery(String value) {
    public VirtualWorkspaceQuery {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("virtual workspace query must not be blank");
        }
    }

    public static VirtualWorkspaceQuery from(StageImageType stageImage, PlayerId playerId) {
        if (stageImage == null) {
            throw new IllegalArgumentException("stage image must not be null");
        }
        if (playerId == null) {
            throw new IllegalArgumentException("player id must not be null");
        }
        return new VirtualWorkspaceQuery("?uid=" + playerId.value().toLowerCase(Locale.ROOT) + "&stage="
                + stageImage.name().toLowerCase(Locale.ROOT));
    }
}
