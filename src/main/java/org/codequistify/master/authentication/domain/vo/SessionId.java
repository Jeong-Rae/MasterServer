package org.codequistify.master.authentication.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record SessionId(UUID value) {
  public SessionId {
    Objects.requireNonNull(value, "sessionId must not be null");
  }

  public static SessionId of(UUID value) {
    return new SessionId(value);
  }

  public static SessionId newId() {
    return new SessionId(UUID.randomUUID());
  }

  public String asString() {
    return value.toString();
  }
}
