package org.codequistify.master.player.domain.rating;

public record Point(long value) {
  public Point {
    if (value < 0) {
      throw new IllegalArgumentException("point must not be negative");
    }
  }

  public Point add(long delta) {
    if (delta < 0) {
      throw new IllegalArgumentException("point delta must not be negative");
    }
    return new Point(value + delta);
  }
}
