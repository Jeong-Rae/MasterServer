package org.codequistify.master.player.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PointTest {
  @Test
  void 포인트가_음수인_상황에서_생성을_하면_예외가_발생해야한다() {
    assertThrows(IllegalArgumentException.class, () -> new Point(-1));
  }

  @Test
  void 양수_포인트에서_음수_증가를_요청하면_예외가_발생해야한다() {
    Point point = new Point(10);
    assertThrows(IllegalArgumentException.class, () -> point.add(-5));
  }

  @Test
  void 양수_포인트에서_양수_증가를_요청하면_합산된_포인트가_생성되어야한다() {
    Point point = new Point(10);
    Point updated = point.add(5);
    assertEquals(15, updated.value());
  }
}
