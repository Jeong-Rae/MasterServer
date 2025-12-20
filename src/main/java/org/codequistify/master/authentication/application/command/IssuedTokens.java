package org.codequistify.master.authentication.application.command;

import java.time.Instant;
import java.util.Objects;

public record IssuedTokens(
    String accessToken, String refreshToken, Instant issuedAt, Instant refreshTokenExpiresAt) {
  public IssuedTokens {
    Objects.requireNonNull(accessToken, "accessToken must not be null");
    Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    Objects.requireNonNull(issuedAt, "issuedAt must not be null");
    Objects.requireNonNull(refreshTokenExpiresAt, "refreshTokenExpiresAt must not be null");
  }
}
