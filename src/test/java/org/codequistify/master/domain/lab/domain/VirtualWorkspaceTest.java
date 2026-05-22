package org.codequistify.master.domain.lab.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;

class VirtualWorkspaceTest {

    @Test
    void VirtualWorkspace를_시작하면_REQUESTED_상태와_runtime_binding을_생성한다() {
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                new PlayerId("Player-ID"), new VirtualWorkspaceStage("1", StageImageType.S1001));

        assertThat(workspace.id().value()).isNotBlank();
        assertThat(workspace.playerId().value()).isEqualTo("Player-ID");
        assertThat(workspace.stage().id()).isEqualTo("1");
        assertThat(workspace.status()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(workspace.runtime().name().value()).isEqualTo("s1001-player-id");
        assertThat(workspace.binding().name().value()).isEqualTo("s1001-player-id");
    }

    @Test
    void PROVISIONING_상태에서_ready_처리하면_RUNNING이_된다() {
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                new PlayerId("player-id"), new VirtualWorkspaceStage("1", StageImageType.S1001));

        workspace.startProvisioning().markRunning();

        assertThat(workspace.status()).isEqualTo(VirtualWorkspaceStatus.RUNNING);
        assertThat(workspace.isRunning()).isTrue();
    }

    @Test
    void RUNNING_상태에서_삭제를_시작하면_DELETING_후_DELETED가_된다() {
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                new PlayerId("player-id"), new VirtualWorkspaceStage("1", StageImageType.S1001));

        workspace.startProvisioning().markRunning().startDeleting().markDeleted();

        assertThat(workspace.status()).isEqualTo(VirtualWorkspaceStatus.DELETED);
        assertThat(workspace.isTerminal()).isTrue();
    }

    @Test
    void 허용되지_않은_상태_전이는_예외를_던진다() {
        VirtualWorkspace workspace = new VirtualWorkspace(
                new VirtualWorkspaceId("workspace-id"),
                new PlayerId("player-id"),
                new VirtualWorkspaceStage("1", StageImageType.S1001),
                VirtualWorkspaceStatus.DELETED,
                null);

        assertThatThrownBy(workspace::startProvisioning).isInstanceOf(IllegalStateException.class);
    }
}
