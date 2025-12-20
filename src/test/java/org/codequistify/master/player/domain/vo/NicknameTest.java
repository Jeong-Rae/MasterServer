package org.codequistify.master.player.domain.vo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NicknameTest {
  @Test
  void 영문과_숫자로_구성된_닉네임을_생성하면_성공해야한다() {
    assertDoesNotThrow(() -> new Nickname("Player01"));
  }

  @Test
  void 공백만_있는_닉네임을_생성하면_예외가_발생해야한다() {
    assertThrows(IllegalArgumentException.class, () -> new Nickname("   "));
  }

  @Test
  void 특수문자가_포함된_닉네임을_생성하면_예외가_발생해야한다() {
    assertThrows(IllegalArgumentException.class, () -> new Nickname("player!"));
  }

  @Test
  void 공백이_포함된_닉네임을_생성하면_앞뒤_공백이_제거되어야한다() {
    Nickname nickname = new Nickname("  Player01  ");
    assertEquals("Player01", nickname.value());
  }

  @Test
  void 길이가_10자를_초과한_닉네임을_생성하면_예외가_발생해야한다() {
    String tooLong = "01234567890";
    assertThrows(IllegalArgumentException.class, () -> new Nickname(tooLong));
  }
}
