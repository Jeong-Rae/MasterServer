package org.codequistify.master.domain.lab.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KubernetesResourceCollectorTest {

    @Mock
    private KubernetesVirtualWorkspaceProvisioner kubernetesVirtualWorkspaceProvisioner;

    @Mock
    private VirtualWorkspaceRepository virtualWorkspaceRepository;

    @Mock
    private VirtualWorkspaceStateChangeLogger virtualWorkspaceStateChangeLogger;

    @Test
    void timeout_runtime은_stage_id_label로_VirtualWorkspace를_찾아_System_회수로_기록한다() {
        KubernetesResourceCollector collector = new KubernetesResourceCollector(
                kubernetesVirtualWorkspaceProvisioner, virtualWorkspaceRepository, virtualWorkspaceStateChangeLogger);
        Pod timeoutPod = pod("s1001-player-id", "player-id", "s1001", "1");
        VirtualWorkspace workspace = runningWorkspace();
        when(kubernetesVirtualWorkspaceProvisioner.getTimeoutRuntimes()).thenReturn(List.of(timeoutPod));
        when(virtualWorkspaceRepository.findLatestActiveByPlayerIdAndStageId(new PlayerId("player-id"), 1L))
                .thenReturn(Optional.of(workspace));
        when(virtualWorkspaceRepository.save(any(VirtualWorkspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        collector.resourceCollection();

        InOrder inOrder = inOrder(
                virtualWorkspaceRepository, virtualWorkspaceStateChangeLogger, kubernetesVirtualWorkspaceProvisioner);
        inOrder.verify(virtualWorkspaceRepository).save(workspace);
        inOrder.verify(virtualWorkspaceStateChangeLogger)
                .recordSuccess(
                        workspace,
                        VirtualWorkspaceEventType.SYSTEM_RECLAIM_STARTED,
                        VirtualWorkspaceStatus.RUNNING,
                        VirtualWorkspaceStatus.DELETING);
        inOrder.verify(kubernetesVirtualWorkspaceProvisioner).remove(workspace);
        inOrder.verify(virtualWorkspaceRepository).save(workspace);
        inOrder.verify(virtualWorkspaceStateChangeLogger)
                .recordSuccess(
                        workspace,
                        VirtualWorkspaceEventType.SYSTEM_RECLAIM_COMPLETED,
                        VirtualWorkspaceStatus.DELETING,
                        VirtualWorkspaceStatus.DELETED);
    }

    @Test
    void stage_id_label이_없는_timeout_runtime은_player와_stage_image로_fallback_조회한다() {
        KubernetesResourceCollector collector = new KubernetesResourceCollector(
                kubernetesVirtualWorkspaceProvisioner, virtualWorkspaceRepository, virtualWorkspaceStateChangeLogger);
        Pod timeoutPod = pod("s1001-player-id", "player-id", "s1001", null);
        VirtualWorkspace workspace = runningWorkspace();
        when(kubernetesVirtualWorkspaceProvisioner.getTimeoutRuntimes()).thenReturn(List.of(timeoutPod));
        when(virtualWorkspaceRepository.findLatestActiveByPlayerIdAndStageImage(
                        new PlayerId("player-id"), StageImageType.S1001))
                .thenReturn(Optional.of(workspace));
        when(virtualWorkspaceRepository.save(any(VirtualWorkspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        collector.resourceCollection();

        verify(virtualWorkspaceRepository)
                .findLatestActiveByPlayerIdAndStageImage(new PlayerId("player-id"), StageImageType.S1001);
        verify(kubernetesVirtualWorkspaceProvisioner).remove(workspace);
    }

    private Pod pod(String name, String player, String stage, String stageId) {
        Map<String, String> labels = new HashMap<>();
        labels.put("player", player);
        labels.put("stage", stage);
        if (stageId != null) {
            labels.put("stage-id", stageId);
        }
        return new PodBuilder()
                .withNewMetadata()
                .withName(name)
                .withLabels(labels)
                .endMetadata()
                .build();
    }

    private VirtualWorkspace runningWorkspace() {
        return VirtualWorkspace.startFor(new PlayerId("player-id"), new VirtualWorkspaceStage(1L, StageImageType.S1001))
                .startProvisioning()
                .markRunning();
    }
}
