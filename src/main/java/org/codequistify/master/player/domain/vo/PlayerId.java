package org.codequistify.master.player.domain.vo;

import java.util.Objects;
import java.util.UUID;

public record PlayerId(UUID value) {
  public PlayerId {
    Objects.requireNonNull(value, "player id must not be null");
  }

  public static PlayerId generate() {
    // NOTE: future migration target is UUIDv7.
    return new PlayerId(UUID.randomUUID());
  }

  public static PlayerId of(UUID value) {
    return new PlayerId(value);
  }

  public static PlayerId from(String value) {
    return new PlayerId(UUID.fromString(value));
  }
}
