package org.codequistify.master.stage.service;

import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.shared.response.SuccessResponse;
import org.codequistify.master.stage.dto.QuestionGradingResponse;
import org.codequistify.master.stage.dto.StageCompletionResponse;
import org.codequistify.master.stage.dto.StageCreateRequest;

public interface StageManagementService {
    // 스테이지 저장
    void saveStage(AuthenticatedPlayer requester, StageCreateRequest request);

    // 문항 채점 요청
    QuestionGradingResponse evaluateAnswer(
            AuthenticatedPlayer requester, String stageId, int questionIndex, String submittedAnswer);

    // 다음 문제 설정 구성
    SuccessResponse composeVirtualWorkspace(AuthenticatedPlayer requester, String stageId, int questionIndex);

    // 풀이 완료 기록
    StageCompletionResponse recordStageComplete(AuthenticatedPlayer requester, String stageId);

    // 풀이 시작 기록
    void recordInProgressStageInit(AuthenticatedPlayer requester, String stageId, int questionIndex);

    // 문제 풀이 정보 기록
    void updateInProgressStage(AuthenticatedPlayer requester, String stageId, int questionIndex);

    // 스테이지 수정
    // 스테이지 문항 수정
    // 스테이지 옵션 수정

}
