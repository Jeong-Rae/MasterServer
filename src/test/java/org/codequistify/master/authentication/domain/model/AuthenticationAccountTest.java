package org.codequistify.master.authentication.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.authentication.domain.vo.LocalCredential;
import org.codequistify.master.authentication.domain.vo.OAuthBinding;
import org.codequistify.master.authentication.domain.vo.PasswordHash;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.junit.jupiter.api.Test;

class AuthenticationAccountTest {
  @Test
  void 로컬_회원가입_요청이_들어오면_계정이_생성되어야한다() {
    PlayerId playerId = PlayerId.of(UUID.randomUUID());
    Email email = Email.of("user@example.com");
    LocalCredential localCredential = LocalCredential.of(PasswordHash.of("hash"));

    AuthenticationAccount account =
        AuthenticationAccount.forLocalRegistration(playerId, email, localCredential);

    assertEquals(playerId, account.playerId());
    assertEquals(email, account.email());
    assertNotNull(account.credentials().localCredential());
    assertEquals(AuthenticationStatus.ACTIVE, account.status());
  }

  @Test
  void 소셜_회원가입_요청이_들어오면_바인딩을_가진_계정이_생성되어야한다() {
    PlayerId playerId = PlayerId.of(UUID.randomUUID());
    Email email = Email.of("user@example.com");
    OAuthBinding binding = OAuthBinding.of(OAuthProvider.GITHUB, "subject");

    AuthenticationAccount account =
        AuthenticationAccount.forOAuthRegistration(playerId, email, binding);

    assertEquals(playerId, account.playerId());
    assertEquals(email, account.email());
    assertEquals(1, account.credentials().oauthBindings().size());
  }
}
