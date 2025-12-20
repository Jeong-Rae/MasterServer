package org.codequistify.master.player.domain.rating;

import java.util.Objects;
import org.codequistify.master.player.domain.PlayerId;

public class Rating {
  private final PlayerId playerId;
  private Point point;

  public Rating(PlayerId playerId, Point point) {
    this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
    this.point = Objects.requireNonNull(point, "point must not be null");
  }

  public PlayerId playerId() {
    return playerId;
  }

  public Point point() {
    return point;
  }

  public void addPoints(long delta) {
    this.point = point.add(delta);
  }
}
