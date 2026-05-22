package org.codequistify.master.domain.lab.service;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.lock.LockManager;
import org.codequistify.master.stage.domain.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class VirtualWorkspaceService {
    private final VirtualWorkspaceProvisioner virtualWorkspaceProvisioner;
    private final VirtualWorkspaceRepository virtualWorkspaceRepository;
    private final VirtualWorkspaceStateChangeLogger virtualWorkspaceStateChangeLogger;
    private final LockManager lockManager;
    private final Logger LOGGER = LoggerFactory.getLogger(VirtualWorkspaceService.class);
    private static final int THRESHOLD = 20;
    private static final int SLEEP_PERIOD = 5000;

    @LogExecutionTime
    public Optional<VirtualWorkspace> requestVirtualWorkspace(PlayerPrincipal principal, Stage stage) {
        ReentrantLock lock =
                lockManager.getLock(principal.id().value(), stage.id().value());
        if (!lock.tryLock()) {
            LOGGER.info(
                    "[requestVirtualWorkspace] apply 작업중 중복된 요청 발생. stage: {}",
                    stage.id().value());
            return Optional.empty();
        }

        try {
            virtualWorkspaceRepository
                    .findLatestActiveByPlayerIdAndStageId(
                            principal.id(), stage.id().value())
                    .ifPresent(this::deleteSync);

            VirtualWorkspace workspace = VirtualWorkspace.startFor(principal.id(), VirtualWorkspaceStage.from(stage));
            workspace = virtualWorkspaceRepository.save(workspace);
            virtualWorkspaceStateChangeLogger.recordRequested(workspace);

            workspace = transition(
                    workspace,
                    VirtualWorkspaceEventType.PROVISIONING_STARTED,
                    VirtualWorkspaceEventType.PROVISIONING_STARTED,
                    VirtualWorkspaceStatus.PROVISIONING,
                    VirtualWorkspace::startProvisioning);

            try {
                virtualWorkspaceProvisioner.provision(workspace);
            } catch (RuntimeException exception) {
                markFailed(workspace, VirtualWorkspaceEventType.FAILED_MARKED, exception.getMessage());
                throw exception;
            }

            LOGGER.info("[requestVirtualWorkspace] stage: {}", stage.id().value());
            return Optional.of(waitUntilRunning(workspace));
        } finally {
            lockManager.unlock(principal.id().value(), stage.id().value());
        }
    }

    @LogExecutionTime
    public void deleteVirtualWorkspaceAsync(PlayerPrincipal principal, Stage stage) {
        virtualWorkspaceRepository
                .findLatestActiveByPlayerIdAndStageId(principal.id(), stage.id().value())
                .ifPresent(this::deleteAsync);
    }

    @LogExecutionTime
    public void deleteVirtualWorkspaceSync(PlayerPrincipal principal, Stage stage) {
        virtualWorkspaceRepository
                .findLatestActiveByPlayerIdAndStageId(principal.id(), stage.id().value())
                .ifPresent(this::deleteSync);
    }

    @LogExecutionTime
    public boolean existsRunningVirtualWorkspace(PlayerPrincipal principal, Stage stage) {
        boolean exists = virtualWorkspaceRepository
                .findRunningByPlayerIdAndStageId(principal.id(), stage.id().value())
                .isPresent();

        LOGGER.info("[existsRunningVirtualWorkspace] exists: {}", exists);
        return exists;
    }

    public VirtualWorkspace getRunningVirtualWorkspace(PlayerPrincipal principal, Stage stage) {
        return virtualWorkspaceRepository
                .findRunningByPlayerIdAndStageId(principal.id(), stage.id().value())
                .orElseThrow(() -> new BusinessException(ErrorCode.VIRTUAL_WORKSPACE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private VirtualWorkspace waitUntilRunning(VirtualWorkspace workspace) {
        int retryCount = 0;
        while (true) {
            if (virtualWorkspaceProvisioner.runtimeReady(workspace)) {
                workspace = transition(
                        workspace,
                        VirtualWorkspaceEventType.RUNNING_MARKED,
                        VirtualWorkspaceEventType.RUNNING_MARKED,
                        VirtualWorkspaceStatus.RUNNING,
                        VirtualWorkspace::markRunning);
                LOGGER.info(
                        "[waitUntilRunning] 네트워크 구성완료, runtime: {}, time: {}ms",
                        workspace.runtime().name().value(),
                        retryCount * 2000);
                return workspace;
            }
            if (retryCount > THRESHOLD) {
                markFailed(
                        workspace,
                        VirtualWorkspaceEventType.FAILED_MARKED,
                        ErrorCode.VIRTUAL_WORKSPACE_CREATE_FAILED.getMessage());
                LOGGER.error("[waitUntilRunning] {}", ErrorCode.VIRTUAL_WORKSPACE_CREATE_FAILED.getMessage());
                throw new BusinessException(
                        ErrorCode.VIRTUAL_WORKSPACE_CREATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                Thread.sleep(2000L);
                retryCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                markFailed(workspace, VirtualWorkspaceEventType.FAILED_MARKED, ErrorCode.FAIL_PROCEED.getMessage());
                LOGGER.info("[waitUntilRunning] 인터럽트 오류 발생");
                throw new BusinessException(ErrorCode.FAIL_PROCEED, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private void deleteAsync(VirtualWorkspace workspace) {
        workspace = transition(
                workspace,
                VirtualWorkspaceEventType.MANUAL_DELETION_STARTED,
                VirtualWorkspaceEventType.MANUAL_DELETION_FAILED,
                VirtualWorkspaceStatus.DELETING,
                VirtualWorkspace::startDeleting);
        try {
            virtualWorkspaceProvisioner.remove(workspace);
        } catch (RuntimeException exception) {
            markFailed(workspace, VirtualWorkspaceEventType.MANUAL_DELETION_FAILED, exception.getMessage());
            throw exception;
        }
    }

    private void deleteSync(VirtualWorkspace workspace) {
        workspace = transition(
                workspace,
                VirtualWorkspaceEventType.MANUAL_DELETION_STARTED,
                VirtualWorkspaceEventType.MANUAL_DELETION_FAILED,
                VirtualWorkspaceStatus.DELETING,
                VirtualWorkspace::startDeleting);
        try {
            virtualWorkspaceProvisioner.remove(workspace);
        } catch (RuntimeException exception) {
            markFailed(workspace, VirtualWorkspaceEventType.MANUAL_DELETION_FAILED, exception.getMessage());
            throw exception;
        }

        boolean runtimeDeleted = false;
        boolean bindingDeleted = false;
        int retryCount = 0;

        while (!runtimeDeleted || !bindingDeleted) {
            if (!runtimeDeleted && !virtualWorkspaceProvisioner.runtimeExists(workspace)) {
                runtimeDeleted = true;
                LOGGER.info("[deleteVirtualWorkspaceSync] Runtime 삭제 확인 {}번 시도", retryCount);
            }
            if (!bindingDeleted && !virtualWorkspaceProvisioner.bindingExists(workspace)) {
                bindingDeleted = true;
                LOGGER.info("[deleteVirtualWorkspaceSync] Binding 삭제 확인 {}번 시도", retryCount);
            }
            if (runtimeDeleted && bindingDeleted) {
                break;
            }

            if (retryCount > THRESHOLD) {
                markFailed(
                        workspace,
                        VirtualWorkspaceEventType.MANUAL_DELETION_FAILED,
                        ErrorCode.VIRTUAL_WORKSPACE_CREATE_FAILED.getMessage());
                LOGGER.error("[deleteVirtualWorkspaceSync] {}", ErrorCode.VIRTUAL_WORKSPACE_CREATE_FAILED.getMessage());
                throw new BusinessException(
                        ErrorCode.VIRTUAL_WORKSPACE_CREATE_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                Thread.sleep(SLEEP_PERIOD);
                retryCount++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                markFailed(
                        workspace,
                        VirtualWorkspaceEventType.MANUAL_DELETION_FAILED,
                        ErrorCode.FAIL_PROCEED.getMessage());
                LOGGER.info("[deleteVirtualWorkspaceSync] 인터럽트 오류 발생");
                throw new BusinessException(ErrorCode.FAIL_PROCEED, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        transition(
                workspace,
                VirtualWorkspaceEventType.MANUAL_DELETION_COMPLETED,
                VirtualWorkspaceEventType.MANUAL_DELETION_FAILED,
                VirtualWorkspaceStatus.DELETED,
                VirtualWorkspace::markDeleted);
    }

    private VirtualWorkspace transition(
            VirtualWorkspace workspace,
            VirtualWorkspaceEventType successEventType,
            VirtualWorkspaceEventType failedEventType,
            VirtualWorkspaceStatus toStatus,
            Consumer<VirtualWorkspace> transition) {
        VirtualWorkspaceStatus fromStatus = workspace.status();
        try {
            transition.accept(workspace);
            VirtualWorkspace saved = virtualWorkspaceRepository.save(workspace);
            virtualWorkspaceStateChangeLogger.recordSuccess(saved, successEventType, fromStatus, toStatus);
            return saved;
        } catch (RuntimeException exception) {
            virtualWorkspaceStateChangeLogger.recordFailure(
                    workspace, failedEventType, fromStatus, toStatus, exception.getMessage());
            throw exception;
        }
    }

    private VirtualWorkspace markFailed(
            VirtualWorkspace workspace, VirtualWorkspaceEventType failedEventType, String reason) {
        VirtualWorkspaceStatus fromStatus = workspace.status();
        workspace.markFailed(reason);
        VirtualWorkspace saved = virtualWorkspaceRepository.save(workspace);
        virtualWorkspaceStateChangeLogger.recordFailure(
                saved, failedEventType, fromStatus, VirtualWorkspaceStatus.FAILED, reason);
        return saved;
    }
}
