package org.codequistify.master.player.domain.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.UUID;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;
import org.junit.jupiter.api.Test;

class AuthorityTest {
  @Test
  void adminBypassesPermissionCheck() {
    Authority authority = new Authority(
        PlayerId.of(UUID.randomUUID()),
        Roles.from(EnumSet.of(Role.ADMIN)),
        Permissions.from(EnumSet.noneOf(Permission.class)));

    assertTrue(authority.hasPermission(Permission.MOCK_TESTS_ACCESS));
  }
}
