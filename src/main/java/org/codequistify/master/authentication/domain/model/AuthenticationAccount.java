package org.codequistify.master.authentication.domain.model;

import java.util.Objects;
import java.util.Set;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.authentication.domain.vo.LocalCredential;
import org.codequistify.master.authentication.domain.vo.OAuthBinding;
import org.codequistify.master.player.domain.vo.PlayerId;

public final class AuthenticationAccount {
  private final PlayerId playerId;
  private final Email email;
  private final Credentials credentials;
  private final AuthenticationStatus status;

  private AuthenticationAccount(
      PlayerId playerId, Email email, Credentials credentials, AuthenticationStatus status) {
    this.playerId = Objects.requireNonNull(playerId, "playerId must not be null");
    this.email = Objects.requireNonNull(email, "email must not be null");
    this.credentials = Objects.requireNonNull(credentials, "credentials must not be null");
    this.status = Objects.requireNonNull(status, "status must not be null");
  }

  public static AuthenticationAccount forLocalRegistration(
      PlayerId playerId, Email email, LocalCredential localCredential) {
    Credentials credentials = Credentials.withLocal(localCredential);
    return new AuthenticationAccount(playerId, email, credentials, AuthenticationStatus.ACTIVE);
  }

  public static AuthenticationAccount forOAuthRegistration(
      PlayerId playerId, Email email, OAuthBinding binding) {
    Credentials credentials = Credentials.of(null, Set.of(binding));
    return new AuthenticationAccount(playerId, email, credentials, AuthenticationStatus.ACTIVE);
  }

  public PlayerId playerId() {
    return playerId;
  }

  public Email email() {
    return email;
  }

  public Credentials credentials() {
    return credentials;
  }

  public AuthenticationStatus status() {
    return status;
  }
}
