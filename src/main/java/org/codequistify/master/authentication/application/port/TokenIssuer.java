package org.codequistify.master.authentication.application.port;

import org.codequistify.master.authentication.application.command.IssuedTokens;
import org.codequistify.master.authentication.application.query.view.AuthorizationView;
import org.codequistify.master.authentication.domain.vo.SessionId;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface TokenIssuer {
  IssuedTokens issueTokens(PlayerId playerId, AuthorizationView authorization, SessionId sessionId);

  IssuedTokens reissueTokens(
      PlayerId playerId, AuthorizationView authorization, SessionId sessionId);
}
