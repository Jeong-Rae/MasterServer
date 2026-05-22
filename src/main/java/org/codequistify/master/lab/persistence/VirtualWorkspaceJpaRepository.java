package org.codequistify.master.domain.lab.persistence;

import java.util.Collection;
import java.util.Optional;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.stage.domain.StageImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualWorkspaceJpaRepository extends JpaRepository<VirtualWorkspaceEntity, String> {
    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageIdAndStatusOrderByUpdatedAtDesc(
            String playerId, String stageId, VirtualWorkspaceStatus status);

    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageIdAndStatusInOrderByUpdatedAtDesc(
            String playerId, String stageId, Collection<VirtualWorkspaceStatus> statuses);

    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageImageAndStatusInOrderByUpdatedAtDesc(
            String playerId, StageImageType stageImage, Collection<VirtualWorkspaceStatus> statuses);
}
