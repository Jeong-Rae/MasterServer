package org.codequistify.master.authentication.domain.model;

import java.time.Instant;
import java.util.Objects;
import org.codequistify.master.authentication.domain.vo.Email;

public final class EmailVerification {
  private final Email email;
  private final String code;
  private final EmailVerificationType type;
  private boolean verified;
  private boolean used;
  private final Instant createdAt;

  private EmailVerification(
      Email email, String code, EmailVerificationType type, Instant createdAt) {
    this.email = Objects.requireNonNull(email, "email must not be null");
    Objects.requireNonNull(code, "code must not be null");
    if (code.isBlank()) {
      throw new IllegalArgumentException("code must not be blank");
    }
    this.code = code;
    this.type = Objects.requireNonNull(type, "type must not be null");
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
  }

  public static EmailVerification of(Email email, String code, EmailVerificationType type) {
    return new EmailVerification(email, code, type, Instant.now());
  }

  public static EmailVerification of(
      Email email, String code, EmailVerificationType type, Instant createdAt) {
    return new EmailVerification(email, code, type, createdAt);
  }

  public Email email() {
    return email;
  }

  public String code() {
    return code;
  }

  public EmailVerificationType type() {
    return type;
  }

  public boolean verified() {
    return verified;
  }

  public boolean used() {
    return used;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public void verify() {
    this.verified = true;
  }

  public void markAsUsed() {
    this.used = true;
  }
}
