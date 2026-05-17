package org.codequistify.master.domain.lab.persistence;

import java.util.Collection;
import java.util.Optional;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualWorkspaceJpaRepository extends JpaRepository<VirtualWorkspaceEntity, String> {
    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageIdAndStatusOrderByModifiedDateDesc(
            String playerId, Long stageId, VirtualWorkspaceStatus status);

    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageIdAndStatusInOrderByModifiedDateDesc(
            String playerId, Long stageId, Collection<VirtualWorkspaceStatus> statuses);

    Optional<VirtualWorkspaceEntity> findFirstByPlayerIdAndStageImageAndStatusInOrderByModifiedDateDesc(
            String playerId, StageImageType stageImage, Collection<VirtualWorkspaceStatus> statuses);
}
