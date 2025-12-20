package org.codequistify.master.authentication.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.codequistify.master.authentication.domain.vo.SessionId;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.junit.jupiter.api.Test;

class SessionTest {
  @Test
  void 만료시각이_지났으면_세션이_만료되어야한다() {
    PlayerId playerId = PlayerId.of(UUID.randomUUID());
    SessionId sessionId = SessionId.of(UUID.randomUUID());
    Instant issuedAt = Instant.parse("2024-01-01T00:00:00Z");
    Instant expiresAt = Instant.parse("2024-01-02T00:00:00Z");

    Session session = Session.of(sessionId, playerId, "refresh-token", issuedAt, expiresAt);

    assertFalse(session.isExpired(Instant.parse("2024-01-01T12:00:00Z")));
    assertTrue(session.isExpired(Instant.parse("2024-01-03T00:00:00Z")));
  }
}
