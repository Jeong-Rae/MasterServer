package org.codequistify.master.stage.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.domain.vo.StageQuestions;
import org.junit.jupiter.api.Test;

class StageTest {

    @Test
    void Stage는_getter_없이_fluent_accessor로_출제_정보를_제공한다() {
        Stage stage = Stage.builder()
                .id(new StageId("1"))
                .title("title")
                .description("description")
                .stageGroup(StageGroupType.BASIC_PROBLEMS)
                .difficultyLevel(DifficultyLevelType.L1)
                .questions(StageQuestions.empty())
                .stageImage(StageImageType.S1001)
                .approved(true)
                .build();

        assertThat(stage.id().value()).isEqualTo("1");
        assertThat(stage.title()).isEqualTo("title");
        assertThat(stage.description()).isEqualTo("description");
        assertThat(stage.stageGroup()).isEqualTo(StageGroupType.BASIC_PROBLEMS);
        assertThat(stage.difficultyLevel()).isEqualTo(DifficultyLevelType.L1);
        assertThat(stage.stageImage()).isEqualTo(StageImageType.S1001);
        assertThat(stage.approved()).isTrue();
    }
}
