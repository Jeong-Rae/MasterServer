package org.codequistify.master.domain.lab.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStateChangeEvent;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VirtualWorkspaceStateChangeLogger {
    private final VirtualWorkspaceEventRepository virtualWorkspaceEventRepository;

    public void recordRequested(VirtualWorkspace workspace) {
        recordSuccess(workspace, VirtualWorkspaceEventType.WORKSPACE_REQUESTED, null, VirtualWorkspaceStatus.REQUESTED);
    }

    public void recordSuccess(
            VirtualWorkspace workspace,
            VirtualWorkspaceEventType eventType,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus) {
        virtualWorkspaceEventRepository.saveIfAbsent(VirtualWorkspaceStateChangeEvent.success(
                workspace.id(),
                workspace.playerId(),
                workspace.stage(),
                eventType,
                fromStatus,
                toStatus,
                LocalDateTime.now(),
                idempotencyKey(workspace, eventType, fromStatus, toStatus)));
    }

    public void recordFailure(
            VirtualWorkspace workspace,
            VirtualWorkspaceEventType eventType,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus,
            String reason) {
        virtualWorkspaceEventRepository.saveIfAbsent(VirtualWorkspaceStateChangeEvent.failed(
                workspace.id(),
                workspace.playerId(),
                workspace.stage(),
                eventType,
                fromStatus,
                toStatus,
                LocalDateTime.now(),
                idempotencyKey(workspace, eventType, fromStatus, toStatus),
                reason));
    }

    private String idempotencyKey(
            VirtualWorkspace workspace,
            VirtualWorkspaceEventType eventType,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus) {
        return workspace.id().value() + ":" + eventType + ":" + fromStatus + ":" + toStatus;
    }
}
