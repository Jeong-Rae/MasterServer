package org.codequistify.master.stage.domain.answer;

import org.codequistify.master.stage.domain.vo.QuestionAnswer;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;

public interface MatchableAnswer {
    boolean matches(SubmittedAnswer submittedAnswer);

    QuestionAnswer correctAnswer();
}
