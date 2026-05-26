package org.codequistify.master.domain.player.controller;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerAuthorizationGuard;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.shared.response.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerAuthorizationController implements PlayerAuthorizationApi {
    private final PlayerAuthorizationGuard playerAuthorizationGuard;

    @Override
    @LogMonitoring
    @GetMapping("me/authorizations/admin")
    public ResponseEntity<BasicResponse> getMyAdminAuthorization(@AuthenticationPrincipal PlayerPrincipal principal) {
        Boolean isAdmin = playerAuthorizationGuard.isAdmin(AuthenticatedPlayer.from(principal));
        return ResponseEntity.ok(BasicResponse.of(isAdmin.toString()));
    }
}
