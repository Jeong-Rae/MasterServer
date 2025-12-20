package org.codequistify.master.player.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.application.port.RatingRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.rating.Rating;
import org.codequistify.master.player.infrastructure.persistence.entity.RatingEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RatingRepositoryAdapter implements RatingRepository {
  private final RatingJpaRepository ratingJpaRepository;

  @Override
  public Optional<Rating> findByPlayerId(PlayerId playerId) {
    return ratingJpaRepository.findById(playerId.value()).map(RatingEntity::toDomain);
  }

  @Override
  public Rating save(Rating rating) {
    return ratingJpaRepository.save(RatingEntity.from(rating)).toDomain();
  }
}
