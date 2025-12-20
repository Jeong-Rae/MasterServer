package org.codequistify.master.authentication.domain.vo;

import java.time.Instant;
import java.util.Objects;

public record LocalCredential(PasswordHash passwordHash, Instant passwordChangedAt) {
  public LocalCredential {
    Objects.requireNonNull(passwordHash, "passwordHash must not be null");
  }

  public static LocalCredential of(PasswordHash passwordHash) {
    return new LocalCredential(passwordHash, null);
  }

  public static LocalCredential of(PasswordHash passwordHash, Instant passwordChangedAt) {
    return new LocalCredential(passwordHash, passwordChangedAt);
  }
}
