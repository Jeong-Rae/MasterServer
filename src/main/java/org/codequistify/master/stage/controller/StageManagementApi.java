package org.codequistify.master.stage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.shared.response.BasicResponse;
import org.codequistify.master.stage.dto.QuestionCommandRequest;
import org.codequistify.master.stage.dto.StageCompletionResponse;
import org.codequistify.master.stage.dto.StageCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Stage")
public interface StageManagementApi {

    @Operation(summary = "스테이지 신규 등록", description = "스테이지 신규 등록")
    ResponseEntity<BasicResponse> createStage(
            @AuthenticationPrincipal PlayerPrincipal principal, @Valid @RequestBody StageCreateRequest request);

    @Operation(summary = "문항 명령 요청", description = """
        문항에 대한 명령을 요청합니다.

        command 목록은 다음과 같습니다.
        - GRADE: 문항을 채점합니다.
        - COMPOSE: 다음 문제를 풀기 위해 필요한 환경을 구성합니다.

        GRADE인 경우 채점 받을 선택을 `submittedAnswer`에 포함합니다.
        """)
    ResponseEntity<?> executeQuestionCommand(
            @AuthenticationPrincipal PlayerPrincipal principal,
            @PathVariable(value = "stage_id") String stageId,
            @PathVariable(value = "question_index") int questionIndex,
            @Valid @RequestBody QuestionCommandRequest request);

    @Operation(summary = "스테이지 풀이 완료 기록", description = "`stage_id` 에 대한 stage 완료를 기록합니다.")
    ResponseEntity<StageCompletionResponse> completeMyStageProgress(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(value = "stage_id") String stageId);
}
