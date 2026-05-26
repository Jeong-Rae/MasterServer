package org.codequistify.master.stage.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageGroupType;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.domain.answer.Answer;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.domain.vo.StageQuestions;
import org.junit.jupiter.api.Test;

class StagePersistenceMapperTest {
    private static final UUID STAGE_UUID = UUID.fromString("019e3d96-7d43-7cb2-b7b4-07677c2295af");

    @Test
    void domainStage를_복합QuestionId와_AnswerEntity_구조로_변환한다() {
        Stage stage = Stage.builder()
                .id(new StageId(STAGE_UUID.toString()))
                .title("title")
                .description("description")
                .stageGroup(StageGroupType.BASIC_PROBLEMS)
                .difficultyLevel(DifficultyLevelType.L1)
                .stageImage(StageImageType.S0000)
                .approved(true)
                .questions(StageQuestions.of(List.of(
                        Question.builder()
                                .index(QuestionIndex.FIRST)
                                .title("choice")
                                .description("choice description")
                                .answer(Answer.of(AnswerType.MULTIPLE_CHOICE, "2", List.of("1", "2"), false))
                                .build(),
                        Question.builder()
                                .index(QuestionIndex.of(2))
                                .title("short")
                                .description("short description")
                                .answer(Answer.of(AnswerType.SHORT_ANSWER, "OK", List.of(), false))
                                .build())))
                .build();

        StageEntity entity = StagePersistenceMapper.toEntity(stage);

        assertThat(entity.id()).isEqualTo(STAGE_UUID);
        assertThat(entity.questionCount()).isEqualTo(2);
        assertThat(entity.questions()).hasSize(2);
        assertThat(entity.questions().get(0).id().stageId())
                .isEqualTo(STAGE_UUID);
        assertThat(entity.questions().get(0).answer().options()).hasSize(2);
        assertThat(entity.questions().get(1).answer().shortAnswerValues()).hasSize(1);
    }

    @Test
    void entityStage를_DomainStage로_복원한다() {
        StageEntity entity = StageEntity.create(
                STAGE_UUID,
                "title",
                "description",
                StageGroupType.BASIC_PROBLEMS,
                DifficultyLevelType.L1,
                StageImageType.S0000,
                StageStatus.APPROVED);
        QuestionEntity question = QuestionEntity.create(
                new QuestionEntityId(entity.id(), 1),
                "choice",
                "choice description",
                AnswerEntity.multipleChoice(
                        new QuestionEntityId(entity.id(), 1),
                        List.of(
                                AnswerOptionEntity.create(new AnswerOptionEntityId(entity.id(), 1, 1), "1", false),
                                AnswerOptionEntity.create(new AnswerOptionEntityId(entity.id(), 1, 2), "2", true))));
        entity.addQuestion(question);

        Stage stage = StagePersistenceMapper.toDomain(entity);

        assertThat(stage.id().value()).isEqualTo(entity.id().toString());
        assertThat(stage.stageQuestions().count()).isEqualTo(1);
        assertThat(stage.stageQuestions().values().get(0).answer().type()).isEqualTo(AnswerType.MULTIPLE_CHOICE);
    }
}
