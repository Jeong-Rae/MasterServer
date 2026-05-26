package org.codequistify.master.stage.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.dto.StagePageResponse;
import org.codequistify.master.stage.dto.StageQuestionResponse;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.dto.StageSearchRequest;
import org.codequistify.master.stage.service.StageManagementService;
import org.codequistify.master.stage.service.StageSearchService;
import org.codequistify.master.stage.utils.HangulExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class StageSearchController implements StageSearchApi {
    private final StageManagementService stageManagementService;
    private final StageSearchService stageSearchService;
    private final Logger LOGGER = LoggerFactory.getLogger(StageSearchController.class);

    // 스테이지 목록 조회
    @Override
    @LogExecutionTime
    @GetMapping("stages")
    public ResponseEntity<StagePageResponse> searchStages(
            @AuthenticationPrincipal PlayerPrincipal principal,
            @Valid @ModelAttribute StageSearchRequest searchRequest) {
        StagePageResponse stages =
                stageSearchService.findStagesByCriteria(searchRequest, AuthenticatedPlayer.from(principal));

        return ResponseEntity.ok(stages);
    }

    @Override
    @LogExecutionTime
    @GetMapping("stages/{stage_id}")
    public ResponseEntity<StageResponse> getStage(@PathVariable(value = "stage_id") String stageId) {
        Stage stage = stageSearchService.getStageById(stageId);
        StageResponse response = StageResponse.from(stage, CompletedStatus.NOT_COMPLETED);

        return ResponseEntity.ok(response);
    }

    // 스테이지 문항 조회 -> 정답은 클라이언트에게 제공 안 됨, 옵션들은 전부 제공되어야 함
    @Override
    @LogMonitoring
    @GetMapping("stages/{stage_id}/questions/{question_index}")
    public ResponseEntity<StageQuestionResponse> getStageQuestion(
            @PathVariable(name = "stage_id") String stageId, @PathVariable(name = "question_index") int questionIndex) {
        StageQuestionResponse response = stageSearchService.findQuestion(stageId, questionIndex);

        LOGGER.info("[getStageQuestion] 문제 조회 완료 id: {}, index: {}", stageId, questionIndex);
        return ResponseEntity.ok(response);
    }

    // 초성 검색
    @Override
    @GetMapping("hangul/choseongs")
    public ResponseEntity<HangulExtractor.ChoCho> extractHangulChoseongs(@RequestParam String src) {
        HangulExtractor hangulExtractor = new HangulExtractor();
        HangulExtractor.ChoCho response = new HangulExtractor.ChoCho(hangulExtractor.extractChoseongs(src));

        return ResponseEntity.ok(response);
    }

    @Override
    @LogMonitoring
    @GetMapping("stage-previews")
    public ResponseEntity<StageResponse> getStagePreview(@RequestParam String query) {
        StageResponse response;
        try {
            for (Character ch : HangulExtractor.choseongs) {
                if (query.contains(ch.toString())) { // 한글 초성이 존재할 때
                    response = stageSearchService.getStageByChoCho(query);
                    return ResponseEntity.ok(response);
                }
            }
            response = stageSearchService.getStageBySearchText(query);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException exception) {
            LOGGER.info("[getStagePreview] 조회된 stage가 존재하지 않습니다.");
            return ResponseEntity.noContent().build();
        }
    }
}
