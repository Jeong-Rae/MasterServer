package org.codequistify.master.domain.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.lock.LockManager;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.domain.vo.StageId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VirtualWorkspaceServiceTest {

    @Mock
    private VirtualWorkspaceProvisioner virtualWorkspaceProvisioner;

    @Mock
    private VirtualWorkspaceRepository virtualWorkspaceRepository;

    @Mock
    private VirtualWorkspaceStateChangeLogger virtualWorkspaceStateChangeLogger;

    @Mock
    private LockManager lockManager;

    @Test
    void VirtualWorkspace_요청은_aggregate를_RUNNING으로_전이하고_Provisioner로_작업공간을_준비한다() {
        VirtualWorkspaceService virtualWorkspaceService = new VirtualWorkspaceService(
                virtualWorkspaceProvisioner,
                virtualWorkspaceRepository,
                virtualWorkspaceStateChangeLogger,
                lockManager);
        PlayerPrincipal principal = principal("player-id");
        Stage stage = stage("1", StageImageType.S1001);
        ReentrantLock lock = new ReentrantLock();
        when(lockManager.getLock(principal.id().value(), stage.id().value())).thenReturn(lock);
        when(virtualWorkspaceRepository.findLatestActiveByPlayerIdAndStageId(
                        principal.id(), stage.id().value()))
                .thenReturn(Optional.empty());
        when(virtualWorkspaceRepository.save(any(VirtualWorkspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(virtualWorkspaceProvisioner.runtimeReady(any(VirtualWorkspace.class)))
                .thenReturn(true);

        Optional<VirtualWorkspace> result = virtualWorkspaceService.requestVirtualWorkspace(principal, stage);

        InOrder inOrder =
                inOrder(virtualWorkspaceRepository, virtualWorkspaceStateChangeLogger, virtualWorkspaceProvisioner);
        inOrder.verify(virtualWorkspaceRepository).save(any(VirtualWorkspace.class));
        inOrder.verify(virtualWorkspaceStateChangeLogger).recordRequested(any(VirtualWorkspace.class));
        inOrder.verify(virtualWorkspaceRepository).save(any(VirtualWorkspace.class));
        inOrder.verify(virtualWorkspaceStateChangeLogger)
                .recordSuccess(
                        any(VirtualWorkspace.class),
                        eq(VirtualWorkspaceEventType.PROVISIONING_STARTED),
                        eq(VirtualWorkspaceStatus.REQUESTED),
                        eq(VirtualWorkspaceStatus.PROVISIONING));
        inOrder.verify(virtualWorkspaceProvisioner).provision(any(VirtualWorkspace.class));
        inOrder.verify(virtualWorkspaceProvisioner).runtimeReady(any(VirtualWorkspace.class));
        inOrder.verify(virtualWorkspaceRepository).save(any(VirtualWorkspace.class));
        inOrder.verify(virtualWorkspaceStateChangeLogger)
                .recordSuccess(
                        any(VirtualWorkspace.class),
                        eq(VirtualWorkspaceEventType.RUNNING_MARKED),
                        eq(VirtualWorkspaceStatus.PROVISIONING),
                        eq(VirtualWorkspaceStatus.RUNNING));
        assertThat(result).isPresent();
        assertThat(result.get().isRunning()).isTrue();
    }

    @Test
    void VirtualWorkspace_존재여부는_DB의_RUNNING_상태를_기준으로_판단한다() {
        VirtualWorkspaceService virtualWorkspaceService = new VirtualWorkspaceService(
                virtualWorkspaceProvisioner,
                virtualWorkspaceRepository,
                virtualWorkspaceStateChangeLogger,
                lockManager);
        PlayerPrincipal principal = principal("player-id");
        Stage stage = stage("1", StageImageType.S1001);
        VirtualWorkspace running = VirtualWorkspace.startFor(principal.id(), VirtualWorkspaceStage.from(stage))
                .startProvisioning()
                .markRunning();
        when(virtualWorkspaceRepository.findRunningByPlayerIdAndStageId(
                        principal.id(), stage.id().value()))
                .thenReturn(Optional.of(running));

        boolean exists = virtualWorkspaceService.existsRunningVirtualWorkspace(principal, stage);

        assertThat(exists).isTrue();
        verifyNoInteractions(virtualWorkspaceProvisioner);
    }

    @Test
    void 사용자_삭제는_Manual_삭제_이벤트로_상태를_전이한다() {
        VirtualWorkspaceService virtualWorkspaceService = new VirtualWorkspaceService(
                virtualWorkspaceProvisioner,
                virtualWorkspaceRepository,
                virtualWorkspaceStateChangeLogger,
                lockManager);
        PlayerPrincipal principal = principal("player-id");
        Stage stage = stage("1", StageImageType.S1001);
        VirtualWorkspace running = VirtualWorkspace.startFor(principal.id(), VirtualWorkspaceStage.from(stage))
                .startProvisioning()
                .markRunning();
        when(virtualWorkspaceRepository.findLatestActiveByPlayerIdAndStageId(
                        principal.id(), stage.id().value()))
                .thenReturn(Optional.of(running));
        when(virtualWorkspaceRepository.save(any(VirtualWorkspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(virtualWorkspaceProvisioner.runtimeExists(running)).thenReturn(false);
        when(virtualWorkspaceProvisioner.bindingExists(running)).thenReturn(false);

        virtualWorkspaceService.deleteVirtualWorkspaceSync(principal, stage);

        InOrder inOrder =
                inOrder(virtualWorkspaceRepository, virtualWorkspaceStateChangeLogger, virtualWorkspaceProvisioner);
        inOrder.verify(virtualWorkspaceRepository).save(running);
        inOrder.verify(virtualWorkspaceStateChangeLogger)
                .recordSuccess(
                        running,
                        VirtualWorkspaceEventType.MANUAL_DELETION_STARTED,
                        VirtualWorkspaceStatus.RUNNING,
                        VirtualWorkspaceStatus.DELETING);
        inOrder.verify(virtualWorkspaceProvisioner).remove(running);
        inOrder.verify(virtualWorkspaceRepository).save(running);
        inOrder.verify(virtualWorkspaceStateChangeLogger)
                .recordSuccess(
                        running,
                        VirtualWorkspaceEventType.MANUAL_DELETION_COMPLETED,
                        VirtualWorkspaceStatus.DELETING,
                        VirtualWorkspaceStatus.DELETED);
    }

    @Test
    void RUNNING_상태의_VirtualWorkspace가_없으면_조회시_NOT_FOUND를_던진다() {
        VirtualWorkspaceService virtualWorkspaceService = new VirtualWorkspaceService(
                virtualWorkspaceProvisioner,
                virtualWorkspaceRepository,
                virtualWorkspaceStateChangeLogger,
                lockManager);
        PlayerPrincipal principal = principal("player-id");
        Stage stage = stage("1", StageImageType.S1001);
        when(virtualWorkspaceRepository.findRunningByPlayerIdAndStageId(
                        principal.id(), stage.id().value()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> virtualWorkspaceService.getRunningVirtualWorkspace(principal, stage))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.VIRTUAL_WORKSPACE_NOT_FOUND);
    }

    private PlayerPrincipal principal(String id) {
        return new PlayerPrincipal(new PlayerId(id), null, false);
    }

    private Stage stage(String id, StageImageType stageImageType) {
        return Stage.builder().id(StageId.of(id)).stageImage(stageImageType).build();
    }
}
