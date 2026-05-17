package org.codequistify.master.domain.lab.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.dto.VirtualWorkspaceCreateResponse;
import org.codequistify.master.domain.lab.dto.VirtualWorkspaceExistsResponse;
import org.codequistify.master.domain.lab.service.VirtualWorkspaceService;
import org.codequistify.master.domain.lab.vo.VirtualWorkspaceHost;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.stage.domain.Stage;
import org.codequistify.master.domain.stage.service.impl.StageSearchServiceImpl;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Lab")
public class LabController {
    private final VirtualWorkspaceService virtualWorkspaceService;
    private final StageSearchServiceImpl stageSearchService;

    private static final VirtualWorkspaceHost LAB_HOST = VirtualWorkspaceHost.lab();

    @Operation(summary = "가상 워크스페이스 (VirtualWorkspace) 생성요청", description = """
        :stage에 대한 워크스페이스(VirtualWorkspace)를 생성한다.
        기존 터미널이 존재할경우, 제거하고 생성한다.
        생성시에 네트워크 연결까지 약 10초 정도가 소요되며,
        제거시에는 약 45s 이상이 소요된다.

        사용시에는 쿼리파라미터 값을 추가해서 connection 연결을 보내야한다.
        """)
    @LogMonitoring
    @PostMapping("lab/terminal/stage/{stage_id}")
    public ResponseEntity<VirtualWorkspaceCreateResponse> applyVirtualWorkspace(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") Long stageId) {
        Stage stage = stageSearchService.getStageById(stageId);

        return virtualWorkspaceService
                .requestVirtualWorkspace(principal, stage)
                .map(workspace -> ResponseEntity.ok(VirtualWorkspaceCreateResponse.from(workspace, LAB_HOST)))
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null));
    }

    // 접속 가능한 주소 조회
    @Operation(summary = "가상 워크스페이스 (VirtualWorkspace) 접속 주소 & 쿼리파라미터 조회", description = """
        :stage에 대한 워크스페이스(VirtualWorkspace)에 접속할 수 있는 쿼리파라미터 정보를 조회한다.

        사용시에는 쿼리파라미터 값을 추가해서 connection 연결을 보내야한다.
        """)
    @GetMapping("lab/terminal/access-url/{stage_id}")
    @LogMonitoring
    public ResponseEntity<VirtualWorkspaceCreateResponse> getVirtualWorkspaceAccessUrl(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") Long stageId) {
        Stage stage = stageSearchService.getStageById(stageId);
        VirtualWorkspace workspace = virtualWorkspaceService.getRunningVirtualWorkspace(principal, stage);

        VirtualWorkspaceCreateResponse response = VirtualWorkspaceCreateResponse.from(workspace, LAB_HOST);

        return ResponseEntity.ok(response);
    }

    // 현재 터미널 존재 여부 조회
    @Operation(summary = "기존 가상 워크스페이스 (VirtualWorkspace) 존재여부 조회", description = """
        :stage에 대한 기존 워크스페이스(VirtualWorkspace)가 존재하는지를 확인한다.
        """)
    @LogExecutionTime
    @GetMapping("/lab/terminal/existence/{stage_id}")
    public ResponseEntity<VirtualWorkspaceExistsResponse> checkVirtualWorkspaceExistence(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") Long stageId) {
        Stage stage = stageSearchService.getStageById(stageId);

        boolean stageExists = virtualWorkspaceService.existsRunningVirtualWorkspace(principal, stage);

        VirtualWorkspaceExistsResponse response = new VirtualWorkspaceExistsResponse(
                principal.id().value(), stageId, stage.getStageImage().name(), stageExists);

        return ResponseEntity.ok(response);
    }
}
