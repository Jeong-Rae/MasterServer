package org.codequistify.master.stage.persistence;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.vo.QuestionId;
import org.codequistify.master.stage.repository.QuestionRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryAdapter implements QuestionRepository {
    private final QuestionJpaRepository questionJpaRepository;

    @Override
    public Optional<Question> findById(QuestionId id) {
        return questionJpaRepository
                .findById(new QuestionEntityId(UUID.fromString(id.stageId().value()), id.index().value()))
                .map(StagePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(QuestionId id) {
        return questionJpaRepository.existsById(
                new QuestionEntityId(UUID.fromString(id.stageId().value()), id.index().value()));
    }
}
