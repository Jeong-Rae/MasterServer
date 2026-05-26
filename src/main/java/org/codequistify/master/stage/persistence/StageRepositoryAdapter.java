package org.codequistify.master.stage.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.repository.StageRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StageRepositoryAdapter implements StageRepository {
    private final StageJpaRepository stageJpaRepository;

    @Override
    public Stage save(Stage stage) {
        StageEntity saved = stageJpaRepository.save(StagePersistenceMapper.toEntity(stage));
        return StagePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Stage> findById(StageId id) {
        return stageJpaRepository.findById(UUID.fromString(id.value())).map(StagePersistenceMapper::toDomain);
    }

    @Override
    public List<Stage> findAll() {
        return stageJpaRepository.findAll().stream()
                .map(StagePersistenceMapper::toDomain)
                .toList();
    }
}
