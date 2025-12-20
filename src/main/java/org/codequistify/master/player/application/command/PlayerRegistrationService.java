package org.codequistify.master.player.application.command;

import java.util.EnumSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.domain.vo.Nickname;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerRegistrationService {
  private final ProfileCommandService profileCommandService;
  private final RatingCommandService ratingCommandService;
  private final AuthorityCommandService authorityCommandService;

  @Transactional
  public void registerNewPlayer(UUID playerUuid, Nickname nickname) {
    profileCommandService.createProfile(playerUuid, nickname.value());
    ratingCommandService.createRating(playerUuid);
    authorityCommandService.createAuthority(
        playerUuid, EnumSet.of(Role.PLAYER), EnumSet.of(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Transactional
  public void registerNewPlayer(UUID playerUuid, String nickname) {
    registerNewPlayer(playerUuid, new Nickname(nickname));
  }

  @Transactional
  public void registerNewPlayer(
      UUID playerUuid, String nicknameCandidate, String fallbackNickname) {
    PlayerId playerId = PlayerId.of(playerUuid);
    Nickname nickname = toNickname(playerId, nicknameCandidate, fallbackNickname);
    registerNewPlayer(playerUuid, nickname);
  }

  private Nickname toNickname(
      PlayerId playerId, String nicknameCandidate, String fallbackNickname) {
    try {
      return new Nickname(nicknameCandidate);
    } catch (IllegalArgumentException exception) {
      return fallbackNickname(playerId, fallbackNickname);
    }
  }

  private Nickname fallbackNickname(PlayerId playerId, String fallbackNickname) {
    try {
      return new Nickname(fallbackNickname);
    } catch (IllegalArgumentException exception) {
      String seed = playerId.value().toString().replace("-", "");
      String generated = "player" + seed.substring(0, 8);
      return new Nickname(generated);
    }
  }
}
