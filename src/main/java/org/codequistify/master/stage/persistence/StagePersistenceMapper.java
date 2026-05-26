package org.codequistify.master.stage.persistence;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.answer.AlwaysCorrectAnswer;
import org.codequistify.master.stage.domain.answer.Answer;
import org.codequistify.master.stage.domain.answer.ExecutionRequired;
import org.codequistify.master.stage.domain.answer.MatchableAnswer;
import org.codequistify.master.stage.domain.answer.OptionSelectable;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.domain.vo.StageQuestions;

public final class StagePersistenceMapper {
    private StagePersistenceMapper() {}

    public static StageEntity toEntity(Stage stage) {
        StageEntity entity = StageEntity.create(
                stage.id() == null ? null : UUID.fromString(stage.id().value()),
                stage.title(),
                stage.description(),
                stage.stageGroup(),
                stage.difficultyLevel(),
                stage.stageImage(),
                stage.approved() ? StageStatus.APPROVED : StageStatus.DRAFT);

        stage.stageQuestions().values().stream()
                .map(question -> toQuestionEntity(entity.id(), question))
                .forEach(entity::addQuestion);

        return entity;
    }

    public static Stage toDomain(StageEntity entity) {
        List<Question> questions = entity.questions().stream()
                .sorted(Comparator.comparingInt(question -> question.id().questionIndex()))
                .map(StagePersistenceMapper::toDomain)
                .toList();

        return Stage.builder()
                .id(StageId.of(entity.id().toString()))
                .title(entity.title())
                .description(entity.description())
                .stageGroup(entity.stageGroup())
                .difficultyLevel(entity.difficultyLevel())
                .stageImage(entity.stageImage())
                .approved(entity.status() == StageStatus.APPROVED)
                .questions(StageQuestions.of(questions))
                .build();
    }

    public static Question toDomain(QuestionEntity entity) {
        AnswerEntity answer = entity.answer();
        return Question.builder()
                .index(QuestionIndex.of(entity.id().questionIndex()))
                .title(entity.title())
                .description(entity.description())
                .answer(toDomainAnswer(answer))
                .build();
    }

    private static QuestionEntity toQuestionEntity(UUID stageId, Question question) {
        QuestionEntityId id = new QuestionEntityId(stageId, question.index().value());
        return QuestionEntity.create(
                id, question.title(), question.description(), toAnswerEntity(id, question.answer()));
    }

    private static AnswerEntity toAnswerEntity(QuestionEntityId id, Answer answer) {
        if (answer instanceof OptionSelectable optionSelectable) {
            return AnswerEntity.multipleChoice(id, toOptionEntities(id, optionSelectable));
        }
        if (answer instanceof MatchableAnswer matchableAnswer) {
            return AnswerEntity.shortAnswer(
                    id,
                    List.of(ShortAnswerValueEntity.create(
                            new ShortAnswerValueEntityId(id.stageId(), id.questionIndex(), 1),
                            matchableAnswer.correctAnswer().value())));
        }
        if (answer instanceof ExecutionRequired executionRequired) {
            return AnswerEntity.practical(id, executionRequired.requiresCompose());
        }
        if (answer instanceof AlwaysCorrectAnswer) {
            return AnswerEntity.description(id);
        }
        return AnswerEntity.description(id);
    }

    private static List<AnswerOptionEntity> toOptionEntities(QuestionEntityId id, OptionSelectable optionSelectable) {
        String correctAnswer = "";
        if (optionSelectable instanceof MatchableAnswer matchableAnswer) {
            correctAnswer = matchableAnswer.correctAnswer().value();
        }

        String finalCorrectAnswer = correctAnswer;
        List<String> optionTexts = optionSelectable.options().rawValues();
        return java.util.stream.IntStream.range(0, optionTexts.size())
                .mapToObj(index -> AnswerOptionEntity.create(
                        new AnswerOptionEntityId(id.stageId(), id.questionIndex(), index + 1),
                        optionTexts.get(index),
                        optionTexts.get(index).equals(finalCorrectAnswer)))
                .toList();
    }

    private static Answer toDomainAnswer(AnswerEntity answer) {
        if (answer.answerType() == AnswerType.MULTIPLE_CHOICE) {
            String correctAnswer = answer.options().stream()
                    .filter(AnswerOptionEntity::isCorrect)
                    .findFirst()
                    .map(AnswerOptionEntity::optionText)
                    .orElse("");
            List<String> options = answer.options().stream()
                    .sorted(Comparator.comparingInt(option -> option.id().optionIndex()))
                    .map(AnswerOptionEntity::optionText)
                    .toList();
            return Answer.of(AnswerType.MULTIPLE_CHOICE, correctAnswer, options, false);
        }
        if (answer.answerType() == AnswerType.SHORT_ANSWER) {
            String correctAnswer = answer.shortAnswerValues().stream()
                    .sorted(Comparator.comparingInt(value -> value.id().answerIndex()))
                    .findFirst()
                    .map(ShortAnswerValueEntity::answerText)
                    .orElse("");
            return Answer.of(AnswerType.SHORT_ANSWER, correctAnswer, List.of(), false);
        }
        if (answer.answerType() == AnswerType.PRACTICAL) {
            return Answer.of(AnswerType.PRACTICAL, "", List.of(), answer.requiresCompose());
        }
        return Answer.of(AnswerType.DESCRIPTION, "", List.of(), false);
    }
}
