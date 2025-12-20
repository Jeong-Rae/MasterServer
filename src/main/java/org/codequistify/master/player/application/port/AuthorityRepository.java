package org.codequistify.master.player.application.port;

import java.util.Optional;
import org.codequistify.master.player.domain.model.Authority;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface AuthorityRepository {
  Optional<Authority> findByPlayerId(PlayerId playerId);

  Authority save(Authority authority);
}
