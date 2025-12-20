package org.codequistify.master.player.presentation;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.command.ProfileCommandService;
import org.codequistify.master.player.application.query.PlayerSummaryQueryService;
import org.codequistify.master.player.application.query.view.PlayerSummaryView;
import org.codequistify.master.player.presentation.dto.ChangeNicknameRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/player-bc")
public class PlayerSummaryController {
  private final PlayerSummaryQueryService playerSummaryQueryService;
  private final ProfileCommandService profileCommandService;

  @GetMapping("/me/summary")
  public ResponseEntity<PlayerSummaryView> getMySummary(
      @AuthenticationPrincipal(expression = "playerUuid") UUID playerUuid) {
    if (playerUuid == null) {
      throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    PlayerSummaryView summary = playerSummaryQueryService.getSummary(playerUuid);
    return ResponseEntity.status(HttpStatus.OK).body(summary);
  }

  @PatchMapping("/me/profile/nickname")
  public ResponseEntity<PlayerSummaryView> changeNickname(
      @AuthenticationPrincipal(expression = "playerUuid") UUID playerUuid,
      @RequestBody ChangeNicknameRequest request) {
    if (playerUuid == null) {
      throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    profileCommandService.changeNickname(playerUuid, request.nickname());
    PlayerSummaryView summary = playerSummaryQueryService.getSummary(playerUuid);
    return ResponseEntity.status(HttpStatus.OK).body(summary);
  }
}
