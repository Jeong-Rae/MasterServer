package org.codequistify.master.stage.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.StageGroupType;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.repository.QuestionRepository;
import org.codequistify.master.stage.repository.StageQueryRepository;
import org.codequistify.master.stage.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StageSearchServiceImplTest {
    @Mock
    private StageRepository stageRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private StageQueryRepository stageQueryRepository;

    @Test
    void 스테이지_요약_조회는_전체_aggregate를_조회하지_않고_projection으로_조회한다() {
        StageSearchServiceImpl service = stageSearchService();
        StageResponse response = stageResponse("018f3f32-6d4e-7b7a-8c9d-1234567890ac", "S1001", "가나다");
        when(stageQueryRepository.findStageSummaryById(response.id())).thenReturn(Optional.of(response));

        assertThat(service.getStageSummary(response.id())).isEqualTo(response);
        verify(stageQueryRepository).findStageSummaryById(response.id());
        verifyNoInteractions(stageRepository);
    }

    @Test
    void 초성_검색은_전체_aggregate를_조회하지_않고_projection_목록에서_찾는다() {
        StageSearchServiceImpl service = stageSearchService();
        StageResponse response = stageResponse("018f3f32-6d4e-7b7a-8c9d-1234567890ac", "S1001", "가나다");
        when(stageQueryRepository.findApprovedStageSummaries()).thenReturn(List.of(response));

        assertThat(service.getStageByChoCho("ㄱㄴ")).isEqualTo(response);
        verify(stageQueryRepository).findApprovedStageSummaries();
        verifyNoInteractions(stageRepository);
    }

    private StageSearchServiceImpl stageSearchService() {
        return new StageSearchServiceImpl(stageRepository, questionRepository, stageQueryRepository);
    }

    private StageResponse stageResponse(String id, String stageCode, String title) {
        return new StageResponse(
                id,
                stageCode,
                title,
                "description",
                StageGroupType.BASIC_PROBLEMS,
                DifficultyLevelType.L1,
                3,
                CompletedStatus.NOT_COMPLETED);
    }
}
