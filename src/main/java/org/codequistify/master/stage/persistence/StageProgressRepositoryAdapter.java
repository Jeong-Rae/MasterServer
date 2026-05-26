package org.codequistify.master.stage.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.PlayerStageSolving;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.repository.StageProgressRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StageProgressRepositoryAdapter implements StageProgressRepository {
    private final StageProgressJpaRepository stageProgressJpaRepository;

    @Override
    public PlayerStageSolving save(PlayerStageSolving solving) {
        PlayerStageProgressEntity saved =
                stageProgressJpaRepository.save(StageProgressPersistenceMapper.toEntity(solving));
        return StageProgressPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<PlayerStageSolving> findByPlayerIdAndStageId(PlayerId playerId, StageId stageId) {
        return stageProgressJpaRepository
                .findById(new PlayerStageProgressEntityId(
                        UUID.fromString(playerId.value()), UUID.fromString(stageId.value())))
                .map(StageProgressPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByPlayerIdAndStageId(PlayerId playerId, StageId stageId) {
        return stageProgressJpaRepository.existsById(
                new PlayerStageProgressEntityId(UUID.fromString(playerId.value()), UUID.fromString(stageId.value())));
    }
}
