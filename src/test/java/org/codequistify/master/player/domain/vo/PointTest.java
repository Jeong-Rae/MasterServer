package org.codequistify.master.player.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PointTest {
  @Test
  void rejectNegativePoint() {
    assertThrows(IllegalArgumentException.class, () -> new Point(-1));
  }

  @Test
  void rejectNegativeDelta() {
    Point point = new Point(10);
    assertThrows(IllegalArgumentException.class, () -> point.add(-5));
  }

  @Test
  void addPositiveDelta() {
    Point point = new Point(10);
    Point updated = point.add(5);
    assertEquals(15, updated.value());
  }
}
