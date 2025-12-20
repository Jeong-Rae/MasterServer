package org.codequistify.master.player.domain.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.codequistify.master.player.domain.vo.Role;

public class Roles {
  private final EnumSet<Role> roles;

  private Roles(EnumSet<Role> roles) {
    this.roles = roles;
  }

  public static Roles from(Set<Role> roles) {
    Objects.requireNonNull(roles, "roles must not be null");
    if (roles.isEmpty()) {
      return new Roles(EnumSet.noneOf(Role.class));
    }
    return new Roles(EnumSet.copyOf(roles));
  }

  public static Roles fromNullable(Set<Role> roles) {
    if (roles == null) {
      return new Roles(EnumSet.noneOf(Role.class));
    }
    return from(roles);
  }

  public EnumSet<Role> asSet() {
    return EnumSet.copyOf(roles);
  }

  public boolean contains(Role role) {
    return roles.contains(Objects.requireNonNull(role, "role must not be null"));
  }

  public void grant(Role role) {
    roles.add(Objects.requireNonNull(role, "role must not be null"));
  }

  public void revoke(Role role) {
    roles.remove(Objects.requireNonNull(role, "role must not be null"));
  }

  public List<String> authorities() {
    return roles.stream().map(Role::authority).collect(Collectors.toList());
  }
}
