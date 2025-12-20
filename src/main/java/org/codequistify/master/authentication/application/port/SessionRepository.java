package org.codequistify.master.authentication.application.port;

import java.util.Optional;
import org.codequistify.master.authentication.domain.model.Session;
import org.codequistify.master.authentication.domain.vo.SessionId;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface SessionRepository {
  Optional<Session> findByRefreshToken(String refreshToken);

  void save(Session session);

  void deleteBySessionId(SessionId sessionId);

  void deleteByPlayerId(PlayerId playerId);
}
