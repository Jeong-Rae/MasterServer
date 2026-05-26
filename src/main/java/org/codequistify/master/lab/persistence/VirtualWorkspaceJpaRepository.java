package org.codequistify.master.domain.lab.persistence;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.stage.domain.StageImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualWorkspaceJpaRepository extends JpaRepository<VirtualWorkspaceEntity, UUID> {
    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageIdAndStatusOrderByUpdatedAtDesc(
            UUID playerId, UUID stageId, VirtualWorkspaceStatus status);

    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageIdAndStatusInOrderByUpdatedAtDesc(
            UUID playerId, UUID stageId, Collection<VirtualWorkspaceStatus> statuses);

    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageImageAndStatusInOrderByUpdatedAtDesc(
            UUID playerId, StageImageType stageImage, Collection<VirtualWorkspaceStatus> statuses);
}
