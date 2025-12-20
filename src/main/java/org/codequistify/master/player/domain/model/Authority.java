package org.codequistify.master.player.domain.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codequistify.master.player.domain.vo.Permission;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.codequistify.master.player.domain.vo.Role;

public class Authority {
  private final PlayerId playerId;
  private final EnumSet<Role> roles;
  private final EnumSet<Permission> permissions;

  public Authority(PlayerId playerId, Set<Role> roles, Set<Permission> permissions) {
    this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
    this.roles = EnumSet.copyOf(Objects.requireNonNull(roles, "roles must not be null"));
    this.permissions =
        EnumSet.copyOf(Objects.requireNonNull(permissions, "permissions must not be null"));
  }

  public PlayerId playerId() {
    return playerId;
  }

  public EnumSet<Role> roles() {
    return EnumSet.copyOf(roles);
  }

  public EnumSet<Permission> permissions() {
    return EnumSet.copyOf(permissions);
  }

  public boolean isAdmin() {
    return roles.contains(Role.ADMIN);
  }

  public boolean hasPermission(Permission permission) {
    return isAdmin() || permissions.contains(permission);
  }

  public void grantRole(Role role) {
    roles.add(Objects.requireNonNull(role, "role must not be null"));
  }

  public void revokeRole(Role role) {
    roles.remove(Objects.requireNonNull(role, "role must not be null"));
  }

  public void grantPermission(Permission permission) {
    permissions.add(Objects.requireNonNull(permission, "permission must not be null"));
  }

  public void revokePermission(Permission permission) {
    permissions.remove(Objects.requireNonNull(permission, "permission must not be null"));
  }

  public List<String> roleAuthorities() {
    return roles.stream().map(Role::authority).collect(Collectors.toList());
  }

  public List<String> permissionAuthorities() {
    return permissions.stream().map(Permission::authority).collect(Collectors.toList());
  }
}
