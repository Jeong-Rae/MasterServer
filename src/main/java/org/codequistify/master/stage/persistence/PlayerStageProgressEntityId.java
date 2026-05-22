package org.codequistify.master.stage.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlayerStageProgressEntityId implements Serializable {
    @Convert(converter = UuidStringBinaryConverter.class)
    @Column(name = "player_id", columnDefinition = "BINARY(16)", nullable = false)
    private String playerId;

    @Convert(converter = UuidStringBinaryConverter.class)
    @Column(name = "stage_id", columnDefinition = "BINARY(16)", nullable = false)
    private String stageId;

    protected PlayerStageProgressEntityId() {}

    public PlayerStageProgressEntityId(String playerId, String stageId) {
        this.playerId = playerId;
        this.stageId = stageId;
    }

    public String playerId() {
        return playerId;
    }

    public String stageId() {
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
