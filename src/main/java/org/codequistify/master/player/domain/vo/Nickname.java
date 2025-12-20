package org.codequistify.master.player.domain.vo;

import java.util.Objects;
import java.util.regex.Pattern;

public record Nickname(String value) {
  private static final int MIN_LENGTH = 1;
  private static final int MAX_LENGTH = 10;
  private static final Pattern POLICY_PATTERN = Pattern.compile("^[a-zA-Z0-9\\uAC00-\\uD7A3]+$");

  public Nickname {
    Objects.requireNonNull(value, "nickname must not be null");
    String normalized = value.trim();
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("nickname must not be blank");
    }
    if (normalized.length() < MIN_LENGTH || normalized.length() > MAX_LENGTH) {
      throw new IllegalArgumentException("nickname length out of range");
    }
    if (!POLICY_PATTERN.matcher(normalized).matches()) {
      throw new IllegalArgumentException("nickname contains invalid characters");
    }
    value = normalized;
  }
}
