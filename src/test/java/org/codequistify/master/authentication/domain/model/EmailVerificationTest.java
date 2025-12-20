package org.codequistify.master.authentication.domain.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.codequistify.master.authentication.domain.vo.Email;
import org.junit.jupiter.api.Test;

class EmailVerificationTest {
  @Test
  void 인증을_완료하면_검증상태와_사용상태가_변경되어야한다() {
    EmailVerification verification = EmailVerification.of(
        Email.of("user@example.com"), "code-123", EmailVerificationType.REGISTRATION);

    assertFalse(verification.verified());
    assertFalse(verification.used());

    verification.verify();
    verification.markAsUsed();

    assertTrue(verification.verified());
    assertTrue(verification.used());
  }
}
