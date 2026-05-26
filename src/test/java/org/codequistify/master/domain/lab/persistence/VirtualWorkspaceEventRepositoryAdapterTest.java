package org.codequistify.master.domain.lab.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStateChangeEvent;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceEventRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({VirtualWorkspaceEventRepositoryAdapter.class, VirtualWorkspaceEventPersistenceMapper.class})
class VirtualWorkspaceEventRepositoryAdapterTest {
    private static final String PLAYER_UUID = "018f3f32-6d4e-7b7a-8c9d-1234567890ab";
    private static final String STAGE_UUID = "018f3f32-6d4e-7b7a-8c9d-1234567890ac";
    private static final String WORKSPACE_UUID = "018f3f32-6d4e-7b7a-8c9d-1234567890ad";

    @Autowired
    private VirtualWorkspaceEventRepository virtualWorkspaceEventRepository;

    @Test
    void VirtualWorkspace_상태변경_이벤트를_workspace_id와_발생시각순으로_조회한다() {
        VirtualWorkspaceId workspaceId = new VirtualWorkspaceId(WORKSPACE_UUID);
        VirtualWorkspaceStateChangeEvent requested = event(
                workspaceId,
                VirtualWorkspaceEventType.WORKSPACE_REQUESTED,
                null,
                VirtualWorkspaceStatus.REQUESTED,
                WORKSPACE_UUID + ":WORKSPACE_REQUESTED:null:REQUESTED",
                LocalDateTime.of(2024, 5, 19, 1, 0));
        VirtualWorkspaceStateChangeEvent provisioning = event(
                workspaceId,
                VirtualWorkspaceEventType.PROVISIONING_STARTED,
                VirtualWorkspaceStatus.REQUESTED,
                VirtualWorkspaceStatus.PROVISIONING,
                WORKSPACE_UUID + ":PROVISIONING_STARTED:REQUESTED:PROVISIONING",
                LocalDateTime.of(2024, 5, 19, 1, 1));

        virtualWorkspaceEventRepository.saveIfAbsent(provisioning);
        virtualWorkspaceEventRepository.saveIfAbsent(requested);

        assertThat(virtualWorkspaceEventRepository.findAllByWorkspaceId(workspaceId))
                .extracting(VirtualWorkspaceStateChangeEvent::event)
                .containsExactly(
                        VirtualWorkspaceEventType.WORKSPACE_REQUESTED, VirtualWorkspaceEventType.PROVISIONING_STARTED);
    }

    @Test
    void 같은_idempotency_key는_중복_저장하지_않고_기존_이벤트를_반환한다() {
        VirtualWorkspaceId workspaceId = new VirtualWorkspaceId(WORKSPACE_UUID);
        VirtualWorkspaceStateChangeEvent first = event(
                workspaceId,
                VirtualWorkspaceEventType.WORKSPACE_REQUESTED,
                null,
                VirtualWorkspaceStatus.REQUESTED,
                "dedup-key",
                LocalDateTime.of(2024, 5, 19, 1, 0));
        VirtualWorkspaceStateChangeEvent duplicated = event(
                workspaceId,
                VirtualWorkspaceEventType.WORKSPACE_REQUESTED,
                null,
                VirtualWorkspaceStatus.REQUESTED,
                "dedup-key",
                LocalDateTime.of(2024, 5, 19, 1, 0));

        VirtualWorkspaceStateChangeEvent saved = virtualWorkspaceEventRepository.saveIfAbsent(first);
        VirtualWorkspaceStateChangeEvent result = virtualWorkspaceEventRepository.saveIfAbsent(duplicated);

        assertThat(result.id()).isEqualTo(saved.id());
        assertThat(virtualWorkspaceEventRepository.findAllByWorkspaceId(workspaceId))
                .hasSize(1);
    }

    private VirtualWorkspaceStateChangeEvent event(
            VirtualWorkspaceId workspaceId,
            VirtualWorkspaceEventType eventType,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus,
            String idempotencyKey,
            LocalDateTime occurredAt) {
        return VirtualWorkspaceStateChangeEvent.success(
                workspaceId,
                new PlayerId(PLAYER_UUID),
                new VirtualWorkspaceStage(STAGE_UUID, StageImageType.S1001),
                eventType,
                fromStatus,
                toStatus,
                occurredAt,
                idempotencyKey);
    }
}
