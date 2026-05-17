package org.codequistify.master.domain.lab.persistence;

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
                new VirtualWorkspaceEventId(entity.getId()),
                new VirtualWorkspaceId(entity.getWorkspaceId()),
                new PlayerId(entity.getPlayerId()),
                new VirtualWorkspaceStage(entity.getStageId(), entity.getStageImage()),
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
                .id(event.id().value())
                .workspaceId(event.workspaceId().value())
                .playerId(event.playerId().value())
                .stageId(event.stage().id())
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
