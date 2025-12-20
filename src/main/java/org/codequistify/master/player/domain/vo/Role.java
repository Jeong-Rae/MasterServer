package org.codequistify.master.player.domain.vo;

public enum Role {
  PLAYER("ROLE_PLAYER"),
  ADMIN("ROLE_ADMIN");

  private final String authority;

  Role(String authority) {
    this.authority = authority;
  }

  public String authority() {
    return authority;
  }
}
