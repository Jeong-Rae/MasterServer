package org.codequistify.master.stage.service;

import java.util.List;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPoint;
import org.codequistify.master.stage.dto.StagePageResponse;
import org.codequistify.master.stage.dto.StageQuestionResponse;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.dto.StageSearchRequest;

public interface StageSearchService {
    // 스테이지 조회
    Stage getStageById(String stageId);

    // 스테이지 목록 조회
    StagePageResponse findStagesByCriteria(StageSearchRequest searchRequest, PlayerPrincipal principal);

    // 스테이지 문항 조회 -> 정답은 클라이언트에게 제공 안 됨, 옵션들은 전부 제공되어야 함
    StageQuestionResponse findQuestion(String stageId, int questionIndex);

    // 완료한 스테이지 목록 조회
    PlayerStageProgressResponse getCompletedStagesByPlayerId(String playerId);

    // 진행중인 스테이지 목록 조회
    PlayerStageProgressResponse getInProgressStagesByPlayerId(String playerId);
    // 완료한 날짜/횟수 기록 조회
    List<StageAttemptHeatMapPoint> getHeatMapDataPointsByModifiedDate(String playerId);

    // preview 메서드
    StageResponse getStageByChoCho(String src);

    StageResponse getStageBySearchText(String searchText);
}
