package org.codequistify.master.player.application.port;

import java.util.Optional;
import org.codequistify.master.player.domain.model.Rating;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface RatingRepository {
  Optional<Rating> findByPlayerId(PlayerId playerId);

  Rating save(Rating rating);
}
