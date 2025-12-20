package org.codequistify.master.authentication.domain.vo;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {
  private static final Pattern SIMPLE_EMAIL_PATTERN =
      Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  public Email {
    Objects.requireNonNull(value, "email must not be null");
    String normalized = value.trim().toLowerCase(Locale.ROOT);
    if (normalized.isEmpty()) {
      throw new IllegalArgumentException("email must not be blank");
    }
    if (!SIMPLE_EMAIL_PATTERN.matcher(normalized).matches()) {
      throw new IllegalArgumentException("email format is invalid");
    }
    value = normalized;
  }

  public static Email of(String value) {
    return new Email(value);
  }
}
