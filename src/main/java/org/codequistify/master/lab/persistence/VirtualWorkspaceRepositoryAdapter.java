package org.codequistify.master.domain.lab.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceRepository;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;
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
    public Optional<VirtualWorkspace> findRunningByPlayerIdAndStageId(PlayerId playerId, String stageId) {
        return virtualWorkspaceJpaRepository
                .findFirstByPlayerIdAndStageIdAndStatusOrderByUpdatedAtDesc(
                        UUID.fromString(playerId.value()), UUID.fromString(stageId), VirtualWorkspaceStatus.RUNNING)
                .map(virtualWorkspacePersistenceMapper::toDomain);
    }

    @Override
    public Optional<VirtualWorkspace> findLatestActiveByPlayerIdAndStageId(PlayerId playerId, String stageId) {
        return virtualWorkspaceJpaRepository
                .findFirstByPlayerIdAndStageIdAndStatusInOrderByUpdatedAtDesc(
                        UUID.fromString(playerId.value()), UUID.fromString(stageId), activeStatuses())
                .map(virtualWorkspacePersistenceMapper::toDomain);
    }

    @Override
    public Optional<VirtualWorkspace> findLatestActiveByPlayerIdAndStageImage(
            PlayerId playerId, StageImageType stageImage) {
        return virtualWorkspaceJpaRepository
                .findFirstByPlayerIdAndStageImageAndStatusInOrderByUpdatedAtDesc(
                        UUID.fromString(playerId.value()), stageImage, activeStatuses())
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
