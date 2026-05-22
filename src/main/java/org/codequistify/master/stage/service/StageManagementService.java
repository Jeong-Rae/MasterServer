package org.codequistify.master.stage.service;

import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.util.SuccessResponse;
import org.codequistify.master.stage.dto.QuestionGradingRequest;
import org.codequistify.master.stage.dto.QuestionGradingResponse;
import org.codequistify.master.stage.dto.StageCompletionResponse;
import org.codequistify.master.stage.dto.StageCreateRequest;

public interface StageManagementService {
    // 스테이지 저장
    void saveStage(StageCreateRequest request);

    // 문항 채점 요청
    QuestionGradingResponse evaluateAnswer(PlayerPrincipal principal, QuestionGradingRequest request);

    // 다음 문제 설정 구성
    SuccessResponse composeVirtualWorkspace(PlayerPrincipal principal, QuestionGradingRequest request);

    // 풀이 완료 기록
    StageCompletionResponse recordStageComplete(PlayerPrincipal principal, String stageId);

    // 풀이 시작 기록
    void recordInProgressStageInit(PlayerPrincipal principal, QuestionGradingRequest request);

    // 문제 풀이 정보 기록
    void updateInProgressStage(PlayerPrincipal principal, QuestionGradingRequest request);

    // 스테이지 수정
    // 스테이지 문항 수정
    // 스테이지 옵션 수정

}
