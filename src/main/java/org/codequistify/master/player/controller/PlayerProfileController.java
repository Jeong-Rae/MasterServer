package org.codequistify.master.domain.player.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.player.service.PlayerProfileService;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerProfileController implements PlayerProfileApi {
    private final PlayerProfileService playerProfileService;
    private final PlayerDetailsService playerDetailsService;

    @Override
    @LogMonitoring
    @GetMapping("")
    public ResponseEntity<List<PlayerProfileResponse>> getAllPlayerProfiles(
            @AuthenticationPrincipal PlayerPrincipal principal) {
        return ResponseEntity.ok(playerProfileService.findAllPlayerProfiles(AuthenticatedPlayer.from(principal)));
    }

    @Override
    @LogMonitoring
    @GetMapping("me")
    public ResponseEntity<PlayerProfileResponse> getMyProfile(@AuthenticationPrincipal PlayerPrincipal principal) {
        PlayerProfileResponse playerProfile = PlayerProfileResponse.from(
                playerDetailsService.findOnePlayerById(principal.id().value()));
        return ResponseEntity.ok(playerProfile);
    }

    @Override
    @GetMapping("me/stages")
    public ResponseEntity<PlayerStageProgressResponse> getMyStagesByStatus(
            @AuthenticationPrincipal PlayerPrincipal principal, @RequestParam CompletedStatus status) {
        return ResponseEntity.ok(playerProfileService.getMyStagesByStatus(AuthenticatedPlayer.from(principal), status));
    }

    @Override
    @GetMapping("me/stage-attempt-heatmap")
    public ResponseEntity<List<StageAttemptHeatMapPointResponse>> getMyStageAttemptHeatmap(
            @AuthenticationPrincipal PlayerPrincipal principal) {
        return ResponseEntity.ok(
                playerProfileService.getHeatMapDataPointsByModifiedDate(AuthenticatedPlayer.from(principal)));
    }
}
