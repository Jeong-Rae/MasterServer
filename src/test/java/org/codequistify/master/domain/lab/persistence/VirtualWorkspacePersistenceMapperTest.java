package org.codequistify.master.domain.lab.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;

class VirtualWorkspacePersistenceMapperTest {

    private final VirtualWorkspacePersistenceMapper mapper = new VirtualWorkspacePersistenceMapper();

    @Test
    void VirtualWorkspace와_Entity는_상태와_id를_보존하며_변환된다() {
        VirtualWorkspace workspace = new VirtualWorkspace(
                new VirtualWorkspaceId("workspace-id"),
                new PlayerId("player-id"),
                new VirtualWorkspaceStage("1", StageImageType.S1001),
                VirtualWorkspaceStatus.RUNNING,
                null);

        VirtualWorkspaceEntity entity = mapper.toEntity(workspace);
        VirtualWorkspace restored = mapper.toDomain(entity);

        assertThat(entity.getId()).isEqualTo("workspace-id");
        assertThat(entity.getPlayerId()).isEqualTo("player-id");
        assertThat(entity.getStageId()).isEqualTo("1");
        assertThat(entity.getStageImage()).isEqualTo(StageImageType.S1001);
        assertThat(entity.getStatus()).isEqualTo(VirtualWorkspaceStatus.RUNNING);

        assertThat(restored.id()).isEqualTo(workspace.id());
        assertThat(restored.playerId()).isEqualTo(workspace.playerId());
        assertThat(restored.stage()).isEqualTo(workspace.stage());
        assertThat(restored.status()).isEqualTo(workspace.status());
    }
}
