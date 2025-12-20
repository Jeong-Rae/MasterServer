package org.codequistify.master.player.application.port;

import java.util.Optional;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.authority.Authority;

public interface AuthorityRepository {
  Optional<Authority> findByPlayerId(PlayerId playerId);

  Authority save(Authority authority);
}
