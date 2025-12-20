package org.codequistify.master.player.application.command;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.port.RatingRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.rating.Point;
import org.codequistify.master.player.domain.rating.Rating;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingCommandService {
  private final RatingRepository ratingRepository;

  @Transactional
  public Rating createRating(PlayerId playerId) {
    return ratingRepository.save(new Rating(playerId, new Point(0)));
  }

  @Transactional
  public Rating addPoints(PlayerId playerId, long points) {
    // TODO: ranking/tier aggregation remains out of scope for this refactor.
    Rating rating =
        ratingRepository
            .findByPlayerId(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    rating.addPoints(points);
    return ratingRepository.save(rating);
  }
}
