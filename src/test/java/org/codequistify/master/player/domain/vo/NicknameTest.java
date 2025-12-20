package org.codequistify.master.player.domain.vo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NicknameTest {
  @Test
  void createValidNickname() {
    assertDoesNotThrow(() -> new Nickname("Player01"));
  }

  @Test
  void rejectBlankNickname() {
    assertThrows(IllegalArgumentException.class, () -> new Nickname("   "));
  }

  @Test
  void rejectInvalidCharacters() {
    assertThrows(IllegalArgumentException.class, () -> new Nickname("player!"));
  }
}
