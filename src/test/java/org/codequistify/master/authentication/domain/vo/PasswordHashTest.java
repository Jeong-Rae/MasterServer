package org.codequistify.master.authentication.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PasswordHashTest {
  @Test
  void 해시값이_비어있지_않으면_생성이_성공해야한다() {
    PasswordHash hash = PasswordHash.of("hash-value");
    assertEquals("hash-value", hash.value());
  }

  @Test
  void 해시값이_공백이면_생성이_실패해야한다() {
    assertThrows(IllegalArgumentException.class, () -> PasswordHash.of(""));
  }
}
