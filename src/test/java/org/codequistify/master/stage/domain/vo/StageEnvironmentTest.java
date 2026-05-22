package org.codequistify.master.stage.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.dto.StageQuestionActionRequest;
import org.junit.jupiter.api.Test;

class StageEnvironmentTest {

    @Test
    void StageEnvironment는_compose와_grade_요청에_필요한_stage_code를_제공한다() {
        StageEnvironment environment = new StageEnvironment(new StageId("1"), StageImageType.S1001);

        assertThat(environment.stageCode()).isEqualTo("S1001");
        assertThat(environment.resourceCode()).isEqualTo("s1001");
    }

    @Test
    void StageQuestionActionRequest는_StageEnvironment와_QuestionIndex로_생성한다() {
        StageEnvironment environment = new StageEnvironment(new StageId("1"), StageImageType.S1001);

        StageQuestionActionRequest request = StageQuestionActionRequest.from(environment, new QuestionIndex(2));

        assertThat(request.stageCode()).isEqualTo("s1001");
        assertThat(request.questionIndex()).isEqualTo(2);
    }
}
