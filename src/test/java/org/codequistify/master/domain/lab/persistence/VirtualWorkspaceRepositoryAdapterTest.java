package org.codequistify.master.domain.lab.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({VirtualWorkspaceRepositoryAdapter.class, VirtualWorkspacePersistenceMapper.class})
class VirtualWorkspaceRepositoryAdapterTest {

    @Autowired
    private VirtualWorkspaceRepository virtualWorkspaceRepository;

    @Test
    void RUNNING_상태의_VirtualWorkspace를_player와_stage로_조회한다() {
        PlayerId playerId = new PlayerId("player-id");
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                        playerId, new VirtualWorkspaceStage(1L, StageImageType.S1001))
                .startProvisioning()
                .markRunning();

        virtualWorkspaceRepository.save(workspace);

        assertThat(virtualWorkspaceRepository.findRunningByPlayerIdAndStageId(playerId, 1L))
                .isPresent()
                .get()
                .extracting(VirtualWorkspace::status)
                .isEqualTo(workspace.status());
    }

    @Test
    void DELETED_상태의_VirtualWorkspace는_RUNNING_조회에서_제외한다() {
        PlayerId playerId = new PlayerId("player-id");
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                        playerId, new VirtualWorkspaceStage(1L, StageImageType.S1001))
                .startProvisioning()
                .markRunning()
                .startDeleting()
                .markDeleted();

        virtualWorkspaceRepository.save(workspace);

        assertThat(virtualWorkspaceRepository.findRunningByPlayerIdAndStageId(playerId, 1L))
                .isEmpty();
    }
}
