package org.codequistify.master.stage.repository;

import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;

public interface QuestionAttemptRepository {
    void record(PlayerId playerId, Question question, SubmittedAnswer submittedAnswer, boolean correct);
}
