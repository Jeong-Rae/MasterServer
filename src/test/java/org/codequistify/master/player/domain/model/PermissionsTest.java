package org.codequistify.master.player.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import org.codequistify.master.player.domain.vo.Permission;
import org.junit.jupiter.api.Test;

class PermissionsTest {
  @Test
  void 권한이_비어있는_상황에서_권한을_확인하면_거절되어야한다() {
    Permissions permissions = Permissions.from(EnumSet.noneOf(Permission.class));

    assertFalse(permissions.contains(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Test
  void 권한을_부여하는_상황에서_권한을_추가하면_포함되어야한다() {
    Permissions permissions = Permissions.from(EnumSet.noneOf(Permission.class));

    permissions.grant(Permission.BASIC_PROBLEMS_ACCESS);

    assertTrue(permissions.contains(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Test
  void 권한을_회수하는_상황에서_권한을_삭제하면_포함되지_말아야한다() {
    Permissions permissions = Permissions.from(EnumSet.of(Permission.BASIC_PROBLEMS_ACCESS));

    permissions.revoke(Permission.BASIC_PROBLEMS_ACCESS);

    assertFalse(permissions.contains(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Test
  void 권한_컬렉션이_없는_상황에서_생성을_하면_예외가_발생해야한다() {
    assertThrows(NullPointerException.class, () -> Permissions.from(null));
  }
}
