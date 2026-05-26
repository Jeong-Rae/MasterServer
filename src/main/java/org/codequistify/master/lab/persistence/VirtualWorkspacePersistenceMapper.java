package org.codequistify.master.domain.lab.persistence;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class VirtualWorkspacePersistenceMapper {
    public VirtualWorkspace toDomain(VirtualWorkspaceEntity entity) {
        return new VirtualWorkspace(
                new VirtualWorkspaceId(entity.getId().toString()),
                new PlayerId(entity.getPlayerId().toString()),
                new VirtualWorkspaceStage(entity.getStageId().toString(), entity.getStageImage()),
                entity.getStatus(),
                entity.getFailureReason());
    }

    public VirtualWorkspaceEntity toEntity(VirtualWorkspace workspace) {
        return VirtualWorkspaceEntity.builder()
                .id(UUID.fromString(workspace.id().value()))
                .playerId(UUID.fromString(workspace.playerId().value()))
                .stageId(UUID.fromString(workspace.stage().id()))
                .stageImage(workspace.stage().image())
                .status(workspace.status())
                .failureReason(workspace.failureReason())
                .build();
    }
}
