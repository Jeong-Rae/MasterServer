package org.codequistify.master.player.application.port;

import java.util.Optional;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.profile.Nickname;
import org.codequistify.master.player.domain.profile.Profile;

public interface ProfileRepository {
  Optional<Profile> findByPlayerId(PlayerId playerId);

  Profile save(Profile profile);

  boolean existsByNickname(Nickname nickname);
}
