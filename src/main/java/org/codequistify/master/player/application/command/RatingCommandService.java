package org.codequistify.master.player.application.command;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.port.RatingRepository;
import org.codequistify.master.player.domain.model.Rating;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingCommandService {
  private final RatingRepository ratingRepository;

  @Transactional
  public Rating createRating(UUID playerUuid) {
    return ratingRepository.save(new Rating(PlayerId.of(playerUuid), new Point(0)));
  }

  @Transactional
  public Rating addPoints(UUID playerUuid, long points) {
    // TODO: ranking/tier aggregation remains out of scope for this refactor.
    PlayerId playerId = PlayerId.of(playerUuid);
    Rating rating = ratingRepository
        .findByPlayerId(playerId)
        .orElseThrow(() -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    rating.addPoints(points);
    return ratingRepository.save(rating);
  }
}
