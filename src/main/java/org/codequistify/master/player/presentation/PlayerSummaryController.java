package org.codequistify.master.player.presentation;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.command.ProfileCommandService;
import org.codequistify.master.player.application.query.PlayerSummaryQueryService;
import org.codequistify.master.player.application.query.view.PlayerSummaryView;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.profile.Nickname;
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
      @AuthenticationPrincipal Player player) {
    if (player.getPlayerUuid() == null) {
      throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    PlayerSummaryView summary =
        playerSummaryQueryService.getSummary(PlayerId.of(player.getPlayerUuid()));
    return ResponseEntity.status(HttpStatus.OK).body(summary);
  }

  @PatchMapping("/me/profile/nickname")
  public ResponseEntity<PlayerSummaryView> changeNickname(
      @AuthenticationPrincipal Player player, @RequestBody ChangeNicknameRequest request) {
    if (player.getPlayerUuid() == null) {
      throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    Nickname nickname = toNickname(request.nickname());
    profileCommandService.changeNickname(PlayerId.of(player.getPlayerUuid()), nickname);
    PlayerSummaryView summary =
        playerSummaryQueryService.getSummary(PlayerId.of(player.getPlayerUuid()));
    return ResponseEntity.status(HttpStatus.OK).body(summary);
  }

  private Nickname toNickname(String nickname) {
    try {
      return new Nickname(nickname);
    } catch (IllegalArgumentException exception) {
      throw new BusinessException(ErrorCode.INVALID_NICKNAME, HttpStatus.BAD_REQUEST, exception);
    }
  }
}
