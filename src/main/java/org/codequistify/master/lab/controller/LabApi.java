package org.codequistify.master.domain.lab.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.codequistify.master.domain.lab.dto.VirtualWorkspaceCreateResponse;
import org.codequistify.master.domain.lab.dto.VirtualWorkspaceExistsResponse;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Lab")
public interface LabApi {

    @Operation(summary = "가상 워크스페이스 (VirtualWorkspace) 생성요청", description = """
        :stage에 대한 워크스페이스(VirtualWorkspace)를 생성한다.
        기존 터미널이 존재할경우, 제거하고 생성한다.
        생성시에 네트워크 연결까지 약 10초 정도가 소요되며,
        제거시에는 약 45s 이상이 소요된다.

        사용시에는 쿼리파라미터 값을 추가해서 connection 연결을 보내야한다.
        """)
    ResponseEntity<VirtualWorkspaceCreateResponse> createMyStageVirtualWorkspace(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") String stageId);

    @Operation(summary = "가상 워크스페이스 (VirtualWorkspace) 접속 주소 & 쿼리파라미터 조회", description = """
        :stage에 대한 워크스페이스(VirtualWorkspace)에 접속할 수 있는 쿼리파라미터 정보를 조회한다.

        사용시에는 쿼리파라미터 값을 추가해서 connection 연결을 보내야한다.
        """)
    ResponseEntity<VirtualWorkspaceCreateResponse> getMyStageVirtualWorkspace(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") String stageId);

    @Operation(summary = "기존 가상 워크스페이스 (VirtualWorkspace) 존재여부 조회", description = """
        :stage에 대한 기존 워크스페이스(VirtualWorkspace)가 존재하는지를 확인한다.
        """)
    ResponseEntity<VirtualWorkspaceExistsResponse> getMyStageVirtualWorkspaceStatus(
            @AuthenticationPrincipal PlayerPrincipal principal, @PathVariable(name = "stage_id") String stageId);
}
