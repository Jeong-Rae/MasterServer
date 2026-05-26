package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PlayerStageProgressEntityId implements Serializable {
    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "stage_id", nullable = false)
    private UUID stageId;

    protected PlayerStageProgressEntityId() {}

    public PlayerStageProgressEntityId(UUID playerId, UUID stageId) {
        this.playerId = playerId;
        this.stageId = stageId;
    }

    public UUID playerId() {
        return playerId;
    }

    public UUID stageId() {
        return stageId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PlayerStageProgressEntityId that)) {
            return false;
        }
        return Objects.equals(playerId, that.playerId) && Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, stageId);
    }
}
