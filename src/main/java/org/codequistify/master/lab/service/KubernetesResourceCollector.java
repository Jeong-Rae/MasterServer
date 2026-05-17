package org.codequistify.master.domain.lab.service;

import io.fabric8.kubernetes.api.model.Pod;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KubernetesResourceCollector {
    private final KubernetesVirtualWorkspaceProvisioner kubernetesVirtualWorkspaceProvisioner;
    private final VirtualWorkspaceRepository virtualWorkspaceRepository;
    private final VirtualWorkspaceStateChangeLogger virtualWorkspaceStateChangeLogger;
    private final Logger LOGGER = LoggerFactory.getLogger(KubernetesResourceCollector.class);

    @Scheduled(cron = "0 0 * * * ?")
    public void resourceCollection() {
        List<Pod> timeoutRuntimes = kubernetesVirtualWorkspaceProvisioner.getTimeoutRuntimes();
        timeoutRuntimes.forEach(this::reclaim);

        List<String> timeoutPods = extractPodNames(timeoutRuntimes);
        LOGGER.info(
                "[resourceCollection] VirtualWorkspace {}개 제거, List: {}", timeoutPods.size(), timeoutPods.toString());
    }

    private void reclaim(Pod pod) {
        Optional<VirtualWorkspace> workspace = findWorkspace(pod);
        if (workspace.isEmpty()) {
            String resourceName = getResourceName(pod);
            kubernetesVirtualWorkspaceProvisioner.deleteRuntime(resourceName);
            kubernetesVirtualWorkspaceProvisioner.deleteBinding(resourceName);
            return;
        }

        VirtualWorkspace reclaiming = transition(
                workspace.get(),
                VirtualWorkspaceEventType.SYSTEM_RECLAIM_STARTED,
                VirtualWorkspaceStatus.DELETING,
                VirtualWorkspace::startDeleting);
        try {
            kubernetesVirtualWorkspaceProvisioner.remove(reclaiming);
        } catch (RuntimeException exception) {
            markSystemReclaimFailed(reclaiming, exception.getMessage());
            throw exception;
        }
        transition(
                reclaiming,
                VirtualWorkspaceEventType.SYSTEM_RECLAIM_COMPLETED,
                VirtualWorkspaceStatus.DELETED,
                VirtualWorkspace::markDeleted);
    }

    private Optional<VirtualWorkspace> findWorkspace(Pod pod) {
        Map<String, String> labels = pod.getMetadata().getLabels();
        if (labels == null || labels.get("player") == null) {
            return Optional.empty();
        }

        PlayerId playerId = new PlayerId(labels.get("player"));
        String stageId = labels.get("stage-id");
        if (stageId != null && !stageId.isBlank()) {
            return findByStageId(playerId, stageId);
        }

        String stage = labels.get("stage");
        if (stage == null || stage.isBlank()) {
            return Optional.empty();
        }
        try {
            return virtualWorkspaceRepository.findLatestActiveByPlayerIdAndStageImage(
                    playerId, StageImageType.valueOf(stage.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<VirtualWorkspace> findByStageId(PlayerId playerId, String stageId) {
        try {
            return virtualWorkspaceRepository.findLatestActiveByPlayerIdAndStageId(playerId, Long.parseLong(stageId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private String getResourceName(Pod pod) {
        return pod.getMetadata().getName();
    }

    private List<String> extractPodNames(List<Pod> pods) {
        return pods.stream().map(this::getResourceName).collect(Collectors.toList());
    }

    private VirtualWorkspace transition(
            VirtualWorkspace workspace,
            VirtualWorkspaceEventType eventType,
            VirtualWorkspaceStatus toStatus,
            Consumer<VirtualWorkspace> transition) {
        VirtualWorkspaceStatus fromStatus = workspace.status();
        try {
            transition.accept(workspace);
            VirtualWorkspace saved = virtualWorkspaceRepository.save(workspace);
            virtualWorkspaceStateChangeLogger.recordSuccess(saved, eventType, fromStatus, toStatus);
            return saved;
        } catch (RuntimeException exception) {
            virtualWorkspaceStateChangeLogger.recordFailure(
                    workspace,
                    VirtualWorkspaceEventType.SYSTEM_RECLAIM_FAILED,
                    fromStatus,
                    toStatus,
                    exception.getMessage());
            throw exception;
        }
    }

    private VirtualWorkspace markSystemReclaimFailed(VirtualWorkspace workspace, String reason) {
        VirtualWorkspaceStatus fromStatus = workspace.status();
        workspace.markFailed(reason);
        VirtualWorkspace saved = virtualWorkspaceRepository.save(workspace);
        virtualWorkspaceStateChangeLogger.recordFailure(
                saved,
                VirtualWorkspaceEventType.SYSTEM_RECLAIM_FAILED,
                fromStatus,
                VirtualWorkspaceStatus.FAILED,
                reason);
        return saved;
    }
}
