package org.codequistify.master.player.domain.authority;

public enum Permission {
  BASIC_PROBLEMS_ACCESS("ROLE_BASIC_PROBLEMS_ACCESS"),
  ADVANCED_PROBLEMS_ACCESS("ROLE_ADVANCED_PROBLEMS_ACCESS"),
  MOCK_TESTS_ACCESS("ROLE_MOCK_TESTS_ACCESS");

  private final String authority;

  Permission(String authority) {
    this.authority = authority;
  }

  public String authority() {
    return authority;
  }
}
