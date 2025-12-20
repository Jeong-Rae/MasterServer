package org.codequistify.master.player.domain.model;

import java.util.List;
import java.util.Objects;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;

public class Authority {
  private final PlayerId playerId;
  private final Roles roles;
  private final Permissions permissions;

  public Authority(PlayerId playerId, Roles roles, Permissions permissions) {
    this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
    this.roles = Objects.requireNonNull(roles, "roles must not be null");
    this.permissions = Objects.requireNonNull(permissions, "permissions must not be null");
  }

  public PlayerId playerId() {
    return playerId;
  }

  public Roles roles() {
    return roles;
  }

  public Permissions permissions() {
    return permissions;
  }

  public boolean isAdmin() {
    return roles.contains(Role.ADMIN);
  }

  public boolean hasPermission(Permission permission) {
    return isAdmin() || permissions.contains(permission);
  }

  public void grantRole(Role role) {
    roles.grant(role);
  }

  public void revokeRole(Role role) {
    roles.revoke(role);
  }

  public void grantPermission(Permission permission) {
    permissions.grant(permission);
  }

  public void revokePermission(Permission permission) {
    permissions.revoke(permission);
  }

  public List<String> roleAuthorities() {
    return roles.authorities();
  }

  public List<String> permissionAuthorities() {
    return permissions.authorities();
  }
}
