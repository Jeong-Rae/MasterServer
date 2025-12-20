package org.codequistify.master.player.application.port;

import java.util.Optional;
import org.codequistify.master.player.domain.model.Profile;
import org.codequistify.master.player.domain.vo.Nickname;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface ProfileRepository {
  Optional<Profile> findByPlayerId(PlayerId playerId);

  Profile save(Profile profile);

  boolean existsByNickname(Nickname nickname);
}
