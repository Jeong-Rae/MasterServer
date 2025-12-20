package org.codequistify.master.authentication.domain.service;

import org.codequistify.master.authentication.domain.model.AuthenticationAccount;
import org.codequistify.master.authentication.domain.model.OAuthProvider;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.authentication.domain.vo.LocalCredential;
import org.codequistify.master.authentication.domain.vo.OAuthBinding;
import org.codequistify.master.authentication.domain.vo.PasswordHash;
import org.codequistify.master.player.domain.vo.PlayerId;

public final class RegistrationDomainService {
  public AuthenticationAccount registerLocal(
      PlayerId playerId, Email email, PasswordHash passwordHash) {
    LocalCredential localCredential = LocalCredential.of(passwordHash);
    return AuthenticationAccount.forLocalRegistration(playerId, email, localCredential);
  }

  public AuthenticationAccount registerOAuth(
      PlayerId playerId, Email email, OAuthProvider provider, String providerSubject) {
    OAuthBinding binding = OAuthBinding.of(provider, providerSubject);
    return AuthenticationAccount.forOAuthRegistration(playerId, email, binding);
  }
}
