package org.codequistify.master.authentication.application.port;

import java.util.Optional;
import org.codequistify.master.authentication.domain.model.AuthenticationAccount;
import org.codequistify.master.authentication.domain.model.OAuthProvider;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.player.domain.vo.PlayerId;

public interface AuthenticationAccountRepository {
  Optional<AuthenticationAccount> findByEmail(Email email);

  Optional<AuthenticationAccount> findByOAuth(OAuthProvider provider, String providerSubject);

  Optional<AuthenticationAccount> findByPlayerId(PlayerId playerId);

  boolean existsByEmail(Email email);

  void save(AuthenticationAccount account);
}
