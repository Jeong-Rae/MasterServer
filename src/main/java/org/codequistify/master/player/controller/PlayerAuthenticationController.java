package org.codequistify.master.domain.player.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.dto.UpdatePasswordRequest;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.shared.response.BasicResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/players")
public class PlayerAuthenticationController implements PlayerAuthenticationApi {
    private final PlayerDetailsService playerDetailsService;
    private final Logger LOGGER = LoggerFactory.getLogger(PlayerAuthenticationController.class);

    @Override
    @LogMonitoring
    @PatchMapping("me/password")
    public ResponseEntity<BasicResponse> updateMyPassword(
            @AuthenticationPrincipal PlayerPrincipal principal, @Valid @RequestBody UpdatePasswordRequest request) {
        playerDetailsService.updatePassword(AuthenticatedPlayer.from(principal), request);
        LOGGER.info("[updatePassword] 비밀번호 재설정 완료, Player: {}", principal.id().value());
        return ResponseEntity.ok(BasicResponse.of("SUCCESS"));
    }

    @Override
    @LogMonitoring
    @PutMapping("me/password")
    public ResponseEntity<BasicResponse> resetMyPassword(
            @AuthenticationPrincipal PlayerPrincipal principal, @Valid @RequestBody UpdatePasswordRequest request) {
        playerDetailsService.resetPassword(AuthenticatedPlayer.from(principal), request);
        LOGGER.info("[resetPassword] 비밀번호 초기화 완료, Player: {}", principal.id().value());
        return ResponseEntity.ok(BasicResponse.of("SUCCESS"));
    }

    @Override
    @LogMonitoring
    @DeleteMapping("me")
    public ResponseEntity<BasicResponse> deleteMyAccount(@AuthenticationPrincipal PlayerPrincipal principal) {
        playerDetailsService.deletePlayer(AuthenticatedPlayer.from(principal));
        LOGGER.info("[deletePlayer] 계정 삭제 완료, Player: {}", principal.id().value());
        return ResponseEntity.ok(BasicResponse.of("SUCCESS"));
    }
}
