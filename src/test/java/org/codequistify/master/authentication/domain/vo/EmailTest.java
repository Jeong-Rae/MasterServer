package org.codequistify.master.authentication.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EmailTest {
  @Test
  void 이메일에_대소문자와_공백이_포함된_경우에는_정규화되어야한다() {
    Email email = Email.of("User@Example.com ");
    assertEquals("user@example.com", email.value());
  }

  @Test
  void 이메일이_공백인_경우에는_생성이_실패해야한다() {
    assertThrows(IllegalArgumentException.class, () -> Email.of("  "));
  }

  @Test
  void 이메일_형식이_잘못된_경우에는_생성이_실패해야한다() {
    assertThrows(IllegalArgumentException.class, () -> Email.of("invalid"));
  }
}
