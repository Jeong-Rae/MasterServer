package org.codequistify.master.authentication.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import org.codequistify.master.authentication.domain.model.AuthenticationAccount;
import org.codequistify.master.authentication.domain.model.OAuthProvider;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.authentication.domain.vo.PasswordHash;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.junit.jupiter.api.Test;

class RegistrationDomainServiceTest {
  @Test
  void 로컬_회원가입_요청이_있으면_계정이_생성되어야한다() {
    RegistrationDomainService service = new RegistrationDomainService();
    PlayerId playerId = PlayerId.of(UUID.randomUUID());
    Email email = Email.of("user@example.com");
    PasswordHash passwordHash = PasswordHash.of("hash");

    AuthenticationAccount account = service.registerLocal(playerId, email, passwordHash);

    assertEquals(playerId, account.playerId());
    assertEquals(email, account.email());
    assertNotNull(account.credentials().localCredential());
  }

  @Test
  void 소셜_회원가입_요청이_있으면_바인딩_계정이_생성되어야한다() {
    RegistrationDomainService service = new RegistrationDomainService();
    PlayerId playerId = PlayerId.of(UUID.randomUUID());
    Email email = Email.of("user@example.com");

    AuthenticationAccount account =
        service.registerOAuth(playerId, email, OAuthProvider.GOOGLE, "subject");

    assertEquals(playerId, account.playerId());
    assertEquals(email, account.email());
    assertEquals(1, account.credentials().oauthBindings().size());
  }
}
