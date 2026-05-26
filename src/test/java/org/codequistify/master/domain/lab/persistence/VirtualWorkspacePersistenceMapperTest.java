package org.codequistify.master.domain.lab.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;

class VirtualWorkspacePersistenceMapperTest {
    private static final UUID WORKSPACE_UUID = UUID.fromString("018f3f32-6d4e-7b7a-8c9d-1234567890ad");
    private static final UUID PLAYER_UUID = UUID.fromString("018f3f32-6d4e-7b7a-8c9d-1234567890ab");
    private static final UUID STAGE_UUID = UUID.fromString("018f3f32-6d4e-7b7a-8c9d-1234567890ac");

    private final VirtualWorkspacePersistenceMapper mapper = new VirtualWorkspacePersistenceMapper();

    @Test
    void VirtualWorkspace와_Entity는_상태와_id를_보존하며_변환된다() {
        VirtualWorkspace workspace = new VirtualWorkspace(
                new VirtualWorkspaceId(WORKSPACE_UUID.toString()),
                new PlayerId(PLAYER_UUID.toString()),
                new VirtualWorkspaceStage(STAGE_UUID.toString(), StageImageType.S1001),
                VirtualWorkspaceStatus.RUNNING,
                null);

        VirtualWorkspaceEntity entity = mapper.toEntity(workspace);
        VirtualWorkspace restored = mapper.toDomain(entity);

        assertThat(entity.getId()).isEqualTo(WORKSPACE_UUID);
        assertThat(entity.getPlayerId()).isEqualTo(PLAYER_UUID);
        assertThat(entity.getStageId()).isEqualTo(STAGE_UUID);
        assertThat(entity.getStageImage()).isEqualTo(StageImageType.S1001);
        assertThat(entity.getStatus()).isEqualTo(VirtualWorkspaceStatus.RUNNING);

        assertThat(restored.id()).isEqualTo(workspace.id());
        assertThat(restored.playerId()).isEqualTo(workspace.playerId());
        assertThat(restored.stage()).isEqualTo(workspace.stage());
        assertThat(restored.status()).isEqualTo(workspace.status());
    }
}
