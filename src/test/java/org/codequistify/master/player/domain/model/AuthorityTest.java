package org.codequistify.master.player.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.UUID;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;
import org.junit.jupiter.api.Test;

class AuthorityTest {
  @Test
  void 관리자_역할이_있는_상황에서_권한을_확인하면_항상_허용되어야한다() {
    Authority authority = new Authority(
        PlayerId.of(UUID.randomUUID()),
        Roles.from(EnumSet.of(Role.ADMIN)),
        Permissions.from(EnumSet.noneOf(Permission.class)));

    assertTrue(authority.hasPermission(Permission.MOCK_TESTS_ACCESS));
  }

  @Test
  void 일반_역할이_권한을_보유한_상황에서_권한을_확인하면_허용되어야한다() {
    Authority authority = new Authority(
        PlayerId.of(UUID.randomUUID()),
        Roles.from(EnumSet.of(Role.PLAYER)),
        Permissions.from(EnumSet.of(Permission.BASIC_PROBLEMS_ACCESS)));

    assertTrue(authority.hasPermission(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Test
  void 일반_역할이_권한을_보유하지_않은_상황에서_권한을_확인하면_거절되어야한다() {
    Authority authority = new Authority(
        PlayerId.of(UUID.randomUUID()),
        Roles.from(EnumSet.of(Role.PLAYER)),
        Permissions.from(EnumSet.noneOf(Permission.class)));

    assertFalse(authority.hasPermission(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Test
  void 역할을_부여받는_상황에서_관리자_역할을_추가하면_관리자여야한다() {
    Authority authority = new Authority(
        PlayerId.of(UUID.randomUUID()),
        Roles.from(EnumSet.of(Role.PLAYER)),
        Permissions.from(EnumSet.noneOf(Permission.class)));

    authority.grantRole(Role.ADMIN);

    assertTrue(authority.isAdmin());
  }

  @Test
  void 권한을_회수하는_상황에서_권한을_삭제하면_권한이_없어야한다() {
    Authority authority = new Authority(
        PlayerId.of(UUID.randomUUID()),
        Roles.from(EnumSet.of(Role.PLAYER)),
        Permissions.from(EnumSet.of(Permission.BASIC_PROBLEMS_ACCESS)));

    authority.revokePermission(Permission.BASIC_PROBLEMS_ACCESS);

    assertFalse(authority.hasPermission(Permission.BASIC_PROBLEMS_ACCESS));
  }

  @Test
  void 플레이어가_없는_상황에서_권한을_생성하면_예외가_발생해야한다() {
    assertThrows(
        NullPointerException.class,
        () -> new Authority(
            null,
            Roles.from(EnumSet.of(Role.PLAYER)),
            Permissions.from(EnumSet.noneOf(Permission.class))));
  }
}
