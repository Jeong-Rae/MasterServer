package org.codequistify.master.authentication.domain.model;

import java.time.Instant;
import java.util.Objects;
import org.codequistify.master.authentication.domain.vo.SessionId;
import org.codequistify.master.player.domain.vo.PlayerId;

public final class Session {
  private final SessionId sessionId;
  private final PlayerId playerId;
  private final String refreshToken;
  private final Instant issuedAt;
  private final Instant expiresAt;

  private Session(
      SessionId sessionId,
      PlayerId playerId,
      String refreshToken,
      Instant issuedAt,
      Instant expiresAt) {
    this.sessionId = Objects.requireNonNull(sessionId, "sessionId must not be null");
    this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
    Objects.requireNonNull(refreshToken, "refreshToken must not be null");
    if (refreshToken.isBlank()) {
      throw new IllegalArgumentException("refreshToken must not be blank");
    }
    this.refreshToken = refreshToken;
    this.issuedAt = Objects.requireNonNull(issuedAt, "issuedAt must not be null");
    this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
  }

  public static Session of(
      SessionId sessionId,
      PlayerId playerId,
      String refreshToken,
      Instant issuedAt,
      Instant expiresAt) {
    return new Session(sessionId, playerId, refreshToken, issuedAt, expiresAt);
  }

  public SessionId sessionId() {
    return sessionId;
  }

  public PlayerId playerId() {
    return playerId;
  }

  public String refreshToken() {
    return refreshToken;
  }

  public Instant issuedAt() {
    return issuedAt;
  }

  public Instant expiresAt() {
    return expiresAt;
  }

  public boolean isExpired(Instant now) {
    return expiresAt.isBefore(now);
  }
}
