package org.codequistify.master.authentication.application.port;

import org.codequistify.master.authentication.application.query.view.AuthorizationView;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface AuthorityQueryPort {
  AuthorizationView getAuthorizationView(PlayerId playerId);
}
