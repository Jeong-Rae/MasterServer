package org.codequistify.master.stage.repository;

import java.util.List;
import java.util.Optional;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPoint;
import org.codequistify.master.stage.dto.StageCodeResponse;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.dto.StageSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StageQueryRepository {
    Page<StageResponse> findStagesByCriteria(StageSearchRequest searchRequest, PlayerId playerId, Pageable pageable);

    List<StageCodeResponse> findStageCodesByProgressStatus(PlayerId playerId, CompletedStatus status);

    List<StageAttemptHeatMapPoint> countAttemptByDate(PlayerId playerId);

    Optional<StageResponse> findPreviewBySearchText(String query);
}
