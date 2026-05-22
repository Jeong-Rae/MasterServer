package org.codequistify.master.stage.repository;

import java.util.Optional;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.vo.QuestionId;

public interface QuestionRepository {
    Optional<Question> findById(QuestionId id);

    boolean existsById(QuestionId id);
}
