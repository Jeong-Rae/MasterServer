package org.codequistify.master.player.domain.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codequistify.master.player.domain.vo.Permission;

public class Permissions {
  private final EnumSet<Permission> permissions;

  private Permissions(EnumSet<Permission> permissions) {
    this.permissions = permissions;
  }

  public static Permissions from(Set<Permission> permissions) {
    Objects.requireNonNull(permissions, "permissions must not be null");
    if (permissions.isEmpty()) {
      return new Permissions(EnumSet.noneOf(Permission.class));
    }
    return new Permissions(EnumSet.copyOf(permissions));
  }

  public static Permissions fromNullable(Set<Permission> permissions) {
    if (permissions == null) {
      return new Permissions(EnumSet.noneOf(Permission.class));
    }
    return from(permissions);
  }

  public EnumSet<Permission> asSet() {
    return EnumSet.copyOf(permissions);
  }

  public boolean contains(Permission permission) {
    return permissions.contains(Objects.requireNonNull(permission, "permission must not be null"));
  }

  public void grant(Permission permission) {
    permissions.add(Objects.requireNonNull(permission, "permission must not be null"));
  }

  public void revoke(Permission permission) {
    permissions.remove(Objects.requireNonNull(permission, "permission must not be null"));
  }

  public List<String> authorities() {
    return permissions.stream().map(Permission::authority).collect(Collectors.toList());
  }
}
