package org.codequistify.master.player.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import org.codequistify.master.player.domain.vo.Role;
import org.junit.jupiter.api.Test;

class RolesTest {
  @Test
  void 역할이_비어있는_상황에서_관리자_여부를_확인하면_거절되어야한다() {
    Roles roles = Roles.from(EnumSet.noneOf(Role.class));

    assertFalse(roles.contains(Role.ADMIN));
  }

  @Test
  void 역할을_부여하는_상황에서_관리자_역할을_추가하면_포함되어야한다() {
    Roles roles = Roles.from(EnumSet.of(Role.PLAYER));

    roles.grant(Role.ADMIN);

    assertTrue(roles.contains(Role.ADMIN));
  }

  @Test
  void 역할을_회수하는_상황에서_관리자_역할을_삭제하면_포함되지_말아야한다() {
    Roles roles = Roles.from(EnumSet.of(Role.ADMIN, Role.PLAYER));

    roles.revoke(Role.ADMIN);

    assertFalse(roles.contains(Role.ADMIN));
  }

  @Test
  void 역할_컬렉션이_없는_상황에서_생성을_하면_예외가_발생해야한다() {
    assertThrows(NullPointerException.class, () -> Roles.from(null));
  }
}
