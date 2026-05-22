package org.codequistify.master.domain.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventResult;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStateChangeEvent;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceEventRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VirtualWorkspaceStateChangeLoggerTest {

    @Mock
    private VirtualWorkspaceEventRepository virtualWorkspaceEventRepository;

    @Test
    void VirtualWorkspace_시작_요청은_REQUESTED_상태의_SUCCESS_이벤트로_기록한다() {
        VirtualWorkspaceStateChangeLogger logger =
                new VirtualWorkspaceStateChangeLogger(virtualWorkspaceEventRepository);
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                new PlayerId("player-id"), new VirtualWorkspaceStage("1", StageImageType.S1001));
        when(virtualWorkspaceEventRepository.saveIfAbsent(any(VirtualWorkspaceStateChangeEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        logger.recordRequested(workspace);

        VirtualWorkspaceStateChangeEvent event = capturedEvent();
        assertThat(workspace.status()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(event.event()).isEqualTo(VirtualWorkspaceEventType.WORKSPACE_REQUESTED);
        assertThat(event.fromStatus()).isNull();
        assertThat(event.toStatus()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(event.result()).isEqualTo(VirtualWorkspaceEventResult.SUCCESS);
        assertThat(event.idempotencyKey()).isEqualTo(workspace.id().value() + ":WORKSPACE_REQUESTED:null:REQUESTED");
    }

    @Test
    void 상태_변경_성공은_전달받은_상태와_이벤트로_기록하고_Aggregate를_변경하지_않는다() {
        VirtualWorkspaceStateChangeLogger logger =
                new VirtualWorkspaceStateChangeLogger(virtualWorkspaceEventRepository);
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                new PlayerId("player-id"), new VirtualWorkspaceStage("1", StageImageType.S1001));
        when(virtualWorkspaceEventRepository.saveIfAbsent(any(VirtualWorkspaceStateChangeEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        logger.recordSuccess(
                workspace,
                VirtualWorkspaceEventType.PROVISIONING_STARTED,
                VirtualWorkspaceStatus.REQUESTED,
                VirtualWorkspaceStatus.PROVISIONING);

        VirtualWorkspaceStateChangeEvent event = capturedEvent();
        assertThat(workspace.status()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(event.event()).isEqualTo(VirtualWorkspaceEventType.PROVISIONING_STARTED);
        assertThat(event.fromStatus()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(event.toStatus()).isEqualTo(VirtualWorkspaceStatus.PROVISIONING);
        assertThat(event.result()).isEqualTo(VirtualWorkspaceEventResult.SUCCESS);
    }

    @Test
    void 상태_변경_실패는_원인과_FAILED_결과를_기록하고_Aggregate를_변경하지_않는다() {
        VirtualWorkspaceStateChangeLogger logger =
                new VirtualWorkspaceStateChangeLogger(virtualWorkspaceEventRepository);
        VirtualWorkspace workspace = VirtualWorkspace.startFor(
                new PlayerId("player-id"), new VirtualWorkspaceStage("1", StageImageType.S1001));
        when(virtualWorkspaceEventRepository.saveIfAbsent(any(VirtualWorkspaceStateChangeEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        logger.recordFailure(
                workspace,
                VirtualWorkspaceEventType.SYSTEM_RECLAIM_FAILED,
                VirtualWorkspaceStatus.REQUESTED,
                VirtualWorkspaceStatus.DELETING,
                "invalid transition");

        VirtualWorkspaceStateChangeEvent event = capturedEvent();
        assertThat(workspace.status()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(event.event()).isEqualTo(VirtualWorkspaceEventType.SYSTEM_RECLAIM_FAILED);
        assertThat(event.fromStatus()).isEqualTo(VirtualWorkspaceStatus.REQUESTED);
        assertThat(event.toStatus()).isEqualTo(VirtualWorkspaceStatus.DELETING);
        assertThat(event.result()).isEqualTo(VirtualWorkspaceEventResult.FAILED);
        assertThat(event.failureReason()).isEqualTo("invalid transition");
    }

    private VirtualWorkspaceStateChangeEvent capturedEvent() {
        ArgumentCaptor<VirtualWorkspaceStateChangeEvent> eventCaptor =
                ArgumentCaptor.forClass(VirtualWorkspaceStateChangeEvent.class);
        verify(virtualWorkspaceEventRepository).saveIfAbsent(eventCaptor.capture());
        return eventCaptor.getValue();
    }
}
