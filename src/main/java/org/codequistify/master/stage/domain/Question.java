package org.codequistify.master.stage.domain;

import java.util.List;
import lombok.Builder;
import lombok.ToString;
import org.codequistify.master.stage.domain.answer.AlwaysCorrectAnswer;
import org.codequistify.master.stage.domain.answer.Answer;
import org.codequistify.master.stage.domain.answer.ExecutionRequired;
import org.codequistify.master.stage.domain.answer.MatchableAnswer;
import org.codequistify.master.stage.domain.vo.QuestionId;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;

@ToString
public class Question {
    private QuestionId id;
    private QuestionIndex index;
    private final String title;
    private final String description;
    private final Answer answer;

    @Builder
    public Question(QuestionId id, QuestionIndex index, String title, String description, Answer answer) {
        this.id = id;
        this.index = id == null ? index : id.index();
        this.title = title;
        this.description = description;
        this.answer = answer == null ? Answer.of(AnswerType.SHORT_ANSWER, "", List.of(), false) : answer;
    }

    public QuestionId id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public QuestionIndex index() {
        return id == null ? index : id.index();
    }

    public boolean isPractical() {
        return answer.type() == AnswerType.PRACTICAL;
    }

    public boolean grade(SubmittedAnswer submittedAnswer) {
        if (answer instanceof MatchableAnswer matchableAnswer) {
            return matchableAnswer.matches(submittedAnswer);
        }
        if (answer instanceof AlwaysCorrectAnswer alwaysCorrectAnswer) {
            return alwaysCorrectAnswer.isCorrect();
        }
        return false;
    }

    public boolean requiresCompose() {
        return answer instanceof ExecutionRequired executionRequired && executionRequired.requiresCompose();
    }

    public Answer answer() {
        return answer;
    }

    public void assignIndex(QuestionIndex index) {
        this.index = index;
    }

    public void assignId(QuestionId id) {
        this.id = id;
        this.index = id.index();
    }
}
