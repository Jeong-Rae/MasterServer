package org.codequistify.master.domain.lab.persistence;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VirtualWorkspaceRepositoryAdapter implements VirtualWorkspaceRepository {
    private final VirtualWorkspaceJpaRepository virtualWorkspaceJpaRepository;
    private final VirtualWorkspacePersistenceMapper virtualWorkspacePersistenceMapper;

    @Override
    public VirtualWorkspace save(VirtualWorkspace virtualWorkspace) {
        VirtualWorkspaceEntity saved =
                virtualWorkspaceJpaRepository.save(virtualWorkspacePersistenceMapper.toEntity(virtualWorkspace));
        return virtualWorkspacePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<VirtualWorkspace> findRunningByPlayerIdAndStageId(PlayerId playerId, Long stageId) {
        return virtualWorkspaceJpaRepository
                .findFirstByPlayerIdAndStageIdAndStatusOrderByModifiedDateDesc(
                        playerId.value(), stageId, VirtualWorkspaceStatus.RUNNING)
                .map(virtualWorkspacePersistenceMapper::toDomain);
    }

    @Override
    public Optional<VirtualWorkspace> findLatestActiveByPlayerIdAndStageId(PlayerId playerId, Long stageId) {
        return virtualWorkspaceJpaRepository
                .findFirstByPlayerIdAndStageIdAndStatusInOrderByModifiedDateDesc(
                        playerId.value(), stageId, activeStatuses())
                .map(virtualWorkspacePersistenceMapper::toDomain);
    }

    @Override
    public Optional<VirtualWorkspace> findLatestActiveByPlayerIdAndStageImage(
            PlayerId playerId, StageImageType stageImage) {
        return virtualWorkspaceJpaRepository
                .findFirstByPlayerIdAndStageImageAndStatusInOrderByModifiedDateDesc(
                        playerId.value(), stageImage, activeStatuses())
                .map(virtualWorkspacePersistenceMapper::toDomain);
    }

    private List<VirtualWorkspaceStatus> activeStatuses() {
        return List.of(
                VirtualWorkspaceStatus.REQUESTED,
                VirtualWorkspaceStatus.PROVISIONING,
                VirtualWorkspaceStatus.RUNNING,
                VirtualWorkspaceStatus.DELETING,
                VirtualWorkspaceStatus.FAILED);
    }
}
