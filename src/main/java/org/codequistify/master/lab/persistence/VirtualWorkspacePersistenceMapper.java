package org.codequistify.master.domain.lab.persistence;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.springframework.stereotype.Component;

@Component
public class VirtualWorkspacePersistenceMapper {
    public VirtualWorkspace toDomain(VirtualWorkspaceEntity entity) {
        return new VirtualWorkspace(
                new VirtualWorkspaceId(entity.getId()),
                new PlayerId(entity.getPlayerId()),
                new VirtualWorkspaceStage(entity.getStageId(), entity.getStageImage()),
                entity.getStatus(),
                entity.getFailureReason());
    }

    public VirtualWorkspaceEntity toEntity(VirtualWorkspace workspace) {
        return VirtualWorkspaceEntity.builder()
                .id(workspace.id().value())
                .playerId(workspace.playerId().value())
                .stageId(workspace.stage().id())
                .stageImage(workspace.stage().image())
                .status(workspace.status())
                .failureReason(workspace.failureReason())
                .build();
    }
}
