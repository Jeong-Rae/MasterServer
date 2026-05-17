package org.codequistify.master.domain.stage.service;

import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.stage.dto.GradingRequest;
import org.codequistify.master.domain.stage.dto.GradingResponse;
import org.codequistify.master.domain.stage.dto.StageCompletionResponse;
import org.codequistify.master.domain.stage.dto.StageRegistryRequest;
import org.codequistify.master.global.util.SuccessResponse;

public interface StageManagementService {
    // 스테이지 저장
    void saveStage(StageRegistryRequest request);

    // 문항 채점 요청
    GradingResponse evaluateAnswer(PlayerPrincipal principal, GradingRequest request);

    // 다음 문제 설정 구성
    SuccessResponse composeVirtualWorkspace(PlayerPrincipal principal, GradingRequest request);

    // 풀이 완료 기록
    StageCompletionResponse recordStageComplete(PlayerPrincipal principal, Long stageId);

    // 풀이 시작 기록
    void recordInProgressStageInit(PlayerPrincipal principal, GradingRequest request);

    // 문제 풀이 정보 기록
    public void updateInProgressStage(PlayerPrincipal principal, GradingRequest request);

    // 스테이지 수정
    // 스테이지 문항 수정
    // 스테이지 옵션 수정

}
