package org.codequistify.master.player.application.port;

import java.util.Optional;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.rating.Rating;

public interface RatingRepository {
  Optional<Rating> findByPlayerId(PlayerId playerId);

  Rating save(Rating rating);
}
