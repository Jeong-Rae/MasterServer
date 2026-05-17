package org.codequistify.master.domain.player.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.player.service.PlayerProfileService;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.util.BasicResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
@Tag(name = "Player Authorization")
public class PlayerAuthorizationController {
    private final PlayerProfileService playerProfileService;

    @Operation(summary = "관리자 권한 확인", description = "해당 유저가 관리자 권한을 가지고 있는지 검증합니다.")
    @LogMonitoring
    @GetMapping("me/admin")
    public ResponseEntity<BasicResponse> checkAdminRole(@AuthenticationPrincipal PlayerPrincipal principal) {
        Boolean isAdmin = playerProfileService.isAdmin(principal);
        return ResponseEntity.status(HttpStatus.OK).body(BasicResponse.of(isAdmin.toString()));
    }
}
