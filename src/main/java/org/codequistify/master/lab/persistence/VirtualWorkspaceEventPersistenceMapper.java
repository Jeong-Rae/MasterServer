package org.codequistify.master.domain.lab.persistence;

import java.util.UUID;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStateChangeEvent;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceEventId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.springframework.stereotype.Component;

@Component
public class VirtualWorkspaceEventPersistenceMapper {
    public VirtualWorkspaceStateChangeEvent toDomain(VirtualWorkspaceEventEntity entity) {
        return new VirtualWorkspaceStateChangeEvent(
                new VirtualWorkspaceEventId(entity.getId().toString()),
                new VirtualWorkspaceId(entity.getWorkspaceId().toString()),
                new PlayerId(entity.getPlayerId().toString()),
                new VirtualWorkspaceStage(entity.getStageId().toString(), entity.getStageImage()),
                entity.getEventType(),
                entity.getFromStatus(),
                entity.getToStatus(),
                entity.getOccurredAt(),
                entity.getResult(),
                entity.getIdempotencyKey(),
                entity.getFailureReason());
    }

    public VirtualWorkspaceEventEntity toEntity(VirtualWorkspaceStateChangeEvent event) {
        return VirtualWorkspaceEventEntity.builder()
                .id(UUID.fromString(event.id().value()))
                .workspaceId(UUID.fromString(event.workspaceId().value()))
                .playerId(UUID.fromString(event.playerId().value()))
                .stageId(UUID.fromString(event.stage().id()))
                .stageImage(event.stage().image())
                .eventType(event.event())
                .fromStatus(event.fromStatus())
                .toStatus(event.toStatus())
                .result(event.result())
                .occurredAt(event.occurredAt())
                .idempotencyKey(event.idempotencyKey())
                .failureReason(event.failureReason())
                .build();
    }
}
