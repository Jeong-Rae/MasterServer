package org.codequistify.master.stage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.shared.response.BasicResponse;
import org.codequistify.master.shared.response.SuccessResponse;
import org.codequistify.master.stage.dto.QuestionCommandRequest;
import org.codequistify.master.stage.dto.QuestionGradingResponse;
import org.codequistify.master.stage.dto.StageCompletionResponse;
import org.codequistify.master.stage.dto.StageCreateRequest;
import org.codequistify.master.stage.service.StageManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class StageManagementController implements StageManagementApi {
    private final StageManagementService stageManagementService;
    private final Logger LOGGER = LoggerFactory.getLogger(StageManagementController.class);

    // 스테이지 등록
    @Override
    @PostMapping("stages")
    @LogMonitoring
    public ResponseEntity<BasicResponse> createStage(
            @AuthenticationPrincipal PlayerPrincipal principal, @Valid @RequestBody StageCreateRequest request) {
        stageManagementService.saveStage(AuthenticatedPlayer.from(principal), request);

        LOGGER.info("[createStage] 스테이지 등록 완료");
        return ResponseEntity.ok(BasicResponse.of("SUCCESS"));
    }

    // 문항 채점 요청
    @Override
    @LogMonitoring
    @PostMapping("stages/{stage_id}/questions/{question_index}/commands")
    public ResponseEntity<?> executeQuestionCommand(
            @AuthenticationPrincipal PlayerPrincipal principal,
            @PathVariable(value = "stage_id") String stageId,
            @PathVariable(value = "question_index") int questionIndex,
            @Valid @RequestBody QuestionCommandRequest request) {
        return switch (request.command()) {
            case GRADE -> gradeQuestion(AuthenticatedPlayer.from(principal), stageId, questionIndex, request);
            case COMPOSE -> composeQuestionWorkspace(AuthenticatedPlayer.from(principal), stageId, questionIndex);
        };
    }

    private ResponseEntity<QuestionGradingResponse> gradeQuestion(
            AuthenticatedPlayer requester, String stageId, int questionIndex, QuestionCommandRequest request) {
        QuestionGradingResponse response =
                stageManagementService.evaluateAnswer(requester, stageId, questionIndex, request.submittedAnswer());
        stageManagementService.recordInProgressStageInit(requester, stageId, questionIndex);
        if (response.correct()) {
            stageManagementService.updateInProgressStage(requester, stageId, questionIndex);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<BasicResponse> composeQuestionWorkspace(
            AuthenticatedPlayer requester, String stageId, int questionIndex) {
        SuccessResponse successResponse =
                stageManagementService.composeVirtualWorkspace(requester, stageId, questionIndex);

        if (successResponse.success().equals(false)) {
            throw new BusinessException(ErrorCode.FAIL_PROCEED, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BasicResponse response = BasicResponse.of("SUCCESS");
        return ResponseEntity.ok(response);
    }

    // 문제 풀이 완료 요청
    @Override
    @PostMapping("players/me/stages/{stage_id}/progress")
    @LogMonitoring
    public ResponseEntity<StageCompletionResponse> completeMyStageProgress(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(value = "stage_id") String stageId) {

        StageCompletionResponse response =
                stageManagementService.recordStageComplete(AuthenticatedPlayer.from(principal), stageId);

        return ResponseEntity.ok(response);
    }

    // 스테이지 수정

    // 스테이지 문항 수정

    // 스테이지 옵션 수정

}
