package org.codequistify.master.player.application.query;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.application.port.RatingRepository;
import org.codequistify.master.player.domain.model.Rating;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingQueryService {
  private final RatingRepository ratingRepository;

  public Optional<Rating> findRating(PlayerId playerId) {
    return ratingRepository.findByPlayerId(playerId);
  }
}
