package org.codequistify.master.stage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.stage.dto.StagePageResponse;
import org.codequistify.master.stage.dto.StageQuestionResponse;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.dto.StageSearchRequest;
import org.codequistify.master.stage.utils.HangulExtractor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Stage")
public interface StageSearchApi {

    @Operation(summary = "스테이지 목록 조회", description = """
        - 스테이지 목록을 페이지 단위로 조회합니다.

        - 검색가능 조건은 다음과 같습니다. *'스테이지 분류', '세부 난이도', '풀이 여부', '검색 텍스트 포함'*

        *풀이여부*
        - completedStatus 와 일치하는 상태를 가지는 문제를 필터링합니다.
            - "COMPLETED" : 풀이완료
            - "NOT_COMPLETED" : 미풀이
            - "IN_PROGRESS" : 풀이 진행중

        *스테이지 분류*
            - stageGroupTypes 과 일치하는 타입을 가지는 문제를 필터링합니다.
            - "BASIC_PROBLEMS" : 기본문제
            - "ADVANCED_PROBLEMS" : 심화문제
            - "MOCK_TESTS" : 모의고사

        *세부난이도*
        - difficultLevels 배열안에 있는 난이도를 가지는 문제를 필터링합니다.
            - "L1", "L2", "L3", "L4", "L5"

        *텍스트 검색*
        - searchText 가 'title', 'description', 'stageCode' 중 일치하는 문제를 필터링합니다

        """)
    ResponseEntity<StagePageResponse> searchStages(
            @AuthenticationPrincipal PlayerPrincipal principal,
            @Valid @ModelAttribute StageSearchRequest searchRequest);

    @Operation(summary = "스테이지 조회", description = "스테이지을 조회합니다. completedStatus는 항상 `null`로 기록 되어 있습니다.")
    ResponseEntity<StageResponse> getStage(@PathVariable(value = "stage_id") String stageId);

    @Operation(summary = "문항 조회", description = "문항을 조회합니다. 'stage 정보'와 '몇 번째 문항' 인지에 대한 정보로 조회합니다.")
    ResponseEntity<StageQuestionResponse> getStageQuestion(
            @PathVariable(name = "stage_id") String stageId, @PathVariable(name = "question_index") int questionIndex);

    ResponseEntity<HangulExtractor.ChoCho> extractHangulChoseongs(@RequestParam String src);

    @Operation(
            summary = "쿼리랑 일치하는 스테이지 1개 조회",
            description = "쿼리랑 일치하는 스테이지를 딱 1개 조회합니다.\n\n" + " 일치하는 결과가 여래개인 경우, 결과가 항상 다를 수 있습니다.\n\n"
                    + "일치하는 결과가 없을 경우 204 NO_CONTENT 를 반환합니다.")
    ResponseEntity<StageResponse> getStagePreview(@RequestParam String query);
}
