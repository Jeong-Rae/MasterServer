package org.codequistify.master.player.domain.profile;

import java.util.Objects;
import org.codequistify.master.player.domain.PlayerId;

public class Profile {
  private final PlayerId playerId;
  private Nickname nickname;

  public Profile(PlayerId playerId, Nickname nickname) {
    this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
    this.nickname = Objects.requireNonNull(nickname, "nickname must not be null");
  }

  public PlayerId playerId() {
    return playerId;
  }

  public Nickname nickname() {
    return nickname;
  }

  public void changeNickname(Nickname nickname) {
    this.nickname = Objects.requireNonNull(nickname, "nickname must not be null");
  }
}
