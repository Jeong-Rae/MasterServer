package org.codequistify.master.player.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.codequistify.master.global.util.BaseTimeEntity;
import org.codequistify.master.player.domain.model.Rating;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Point;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "player_rating")
public class RatingEntity extends BaseTimeEntity {
  @Id
  @Column(name = "player_id", nullable = false, columnDefinition = "char(36)")
  private UUID playerId;

  @Column(name = "point", nullable = false)
  private long point;

  public static RatingEntity from(Rating rating) {
    return RatingEntity.builder()
        .playerId(rating.playerId().value())
        .point(rating.point().value())
        .build();
  }

  public Rating toDomain() {
    return new Rating(PlayerId.of(playerId), new Point(point));
  }
}
