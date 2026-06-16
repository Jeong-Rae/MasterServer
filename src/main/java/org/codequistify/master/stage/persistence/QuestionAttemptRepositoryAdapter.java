package org.codequistify.master.stage.persistence;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;
import org.codequistify.master.stage.repository.QuestionAttemptRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionAttemptRepositoryAdapter implements QuestionAttemptRepository {
    private final QuestionAttemptJpaRepository questionAttemptJpaRepository;

    @Override
    public void record(PlayerId playerId, Question question, SubmittedAnswer submittedAnswer, boolean correct) {
        questionAttemptJpaRepository.save(QuestionAttemptEntity.create(
                UUID.fromString(playerId.value()),
                UUID.fromString(question.id().stageId().value()),
                question.index().value(),
                question.answer().type(),
                submittedAnswer.value(),
                correct));
    }
}
