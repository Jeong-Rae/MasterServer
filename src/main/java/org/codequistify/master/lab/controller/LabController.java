package org.codequistify.master.domain.lab.controller;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.dto.VirtualWorkspaceCreateResponse;
import org.codequistify.master.domain.lab.dto.VirtualWorkspaceExistsResponse;
import org.codequistify.master.domain.lab.service.VirtualWorkspaceService;
import org.codequistify.master.domain.lab.vo.VirtualWorkspaceHost;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.service.StageSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/players/me/stages/{stage_id}")
public class LabController implements LabApi {
    private final VirtualWorkspaceService virtualWorkspaceService;
    private final StageSearchService stageSearchService;

    private static final VirtualWorkspaceHost LAB_HOST = VirtualWorkspaceHost.lab();

    @Override
    @LogMonitoring
    @PostMapping("virtual-workspaces")
    public ResponseEntity<VirtualWorkspaceCreateResponse> createMyStageVirtualWorkspace(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") String stageId) {
        Stage stage = stageSearchService.getStageById(stageId);

        return virtualWorkspaceService
                .requestVirtualWorkspace(AuthenticatedPlayer.from(principal), stage)
                .map(workspace -> ResponseEntity.ok(VirtualWorkspaceCreateResponse.from(workspace, LAB_HOST)))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null));
    }

    // 접속 가능한 주소 조회
    @Override
    @GetMapping("virtual-workspace")
    @LogMonitoring
    public ResponseEntity<VirtualWorkspaceCreateResponse> getMyStageVirtualWorkspace(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") String stageId) {
        Stage stage = stageSearchService.getStageById(stageId);
        VirtualWorkspace workspace =
                virtualWorkspaceService.getRunningVirtualWorkspace(AuthenticatedPlayer.from(principal), stage);

        VirtualWorkspaceCreateResponse response = VirtualWorkspaceCreateResponse.from(workspace, LAB_HOST);

        return ResponseEntity.ok(response);
    }

    // 현재 터미널 존재 여부 조회
    @Override
    @LogExecutionTime
    @GetMapping("virtual-workspace/status")
    public ResponseEntity<VirtualWorkspaceExistsResponse> getMyStageVirtualWorkspaceStatus(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") String stageId) {
        Stage stage = stageSearchService.getStageById(stageId);

        boolean stageExists =
                virtualWorkspaceService.existsRunningVirtualWorkspace(AuthenticatedPlayer.from(principal), stage);

        VirtualWorkspaceExistsResponse response =
                new VirtualWorkspaceExistsResponse(principal.id().value(), stageId, stage.stageCode(), stageExists);

        return ResponseEntity.ok(response);
    }
}
