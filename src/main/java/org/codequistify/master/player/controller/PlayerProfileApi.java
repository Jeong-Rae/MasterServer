package org.codequistify.master.domain.player.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Player Profile")
public interface PlayerProfileApi {

    ResponseEntity<List<PlayerProfileResponse>> getAllPlayerProfiles(
            @AuthenticationPrincipal PlayerPrincipal principal);

    @Operation(summary = "자신의 profile 조회", description = "자신의 profile 정보를 응답받는다. 자신이란 현재 소유중인 엑세스 토큰에 기반한다.")
    ResponseEntity<PlayerProfileResponse> getMyProfile(@AuthenticationPrincipal PlayerPrincipal principal);

    @Operation(
            summary = "자신의 문제풀이 상태를 조회할 수 있다.",
            description = "자신의 문제풀이 상태를 조회할 수 있다. 자신이란 현재 소유중인 엑세스 토큰에 기반한다.\n\n" + "*NOT_COMPLETED*는 허용하지 않는다."
                    + " *uri 정보는 현재 제공되지 않는다.*")
    ResponseEntity<PlayerStageProgressResponse> getMyStagesByStatus(
            @AuthenticationPrincipal PlayerPrincipal principal, @RequestParam CompletedStatus status);

    @Operation(summary = "자신의 문제풀이 heatmap 정보 조회", description = "자신의 문제풀이 내역에 대한 heatmap 정보를 조회한다")
    ResponseEntity<List<StageAttemptHeatMapPointResponse>> getMyStageAttemptHeatmap(
            @AuthenticationPrincipal PlayerPrincipal principal);
}
