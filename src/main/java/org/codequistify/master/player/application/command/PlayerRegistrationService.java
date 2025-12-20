package org.codequistify.master.player.application.command;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.authority.Permission;
import org.codequistify.master.player.domain.authority.Role;
import org.codequistify.master.player.domain.profile.Nickname;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerRegistrationService {
  private final ProfileCommandService profileCommandService;
  private final RatingCommandService ratingCommandService;
  private final AuthorityCommandService authorityCommandService;

  @Transactional
  public void registerNewPlayer(PlayerId playerId, Nickname nickname) {
    profileCommandService.createProfile(playerId, nickname);
    ratingCommandService.createRating(playerId);
    authorityCommandService.createAuthority(
        playerId,
        EnumSet.of(Role.PLAYER),
        EnumSet.of(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Transactional
  public void registerNewPlayer(
      PlayerId playerId, String nicknameCandidate, String fallbackNickname) {
    Nickname nickname = toNickname(playerId, nicknameCandidate, fallbackNickname);
    registerNewPlayer(playerId, nickname);
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
