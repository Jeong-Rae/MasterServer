package org.codequistify.master.domain.lab.vo;

import java.util.Locale;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;

public record KubernetesResourceName(String value) {
    public KubernetesResourceName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("kubernetes resource name must not be blank");
        }
    }

    public static KubernetesResourceName from(StageImageType stageImage, PlayerId playerId) {
        if (stageImage == null) {
            throw new IllegalArgumentException("stage image must not be null");
        }
        if (playerId == null) {
            throw new IllegalArgumentException("player id must not be null");
        }
        return new KubernetesResourceName(stageImage.name().toLowerCase(Locale.ROOT) + "-"
                + playerId.value().toLowerCase(Locale.ROOT));
    }
}
