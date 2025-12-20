package org.codequistify.master.authentication.domain.vo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.codequistify.master.authentication.domain.model.OAuthProvider;
import org.junit.jupiter.api.Test;

class OAuthBindingTest {
  @Test
  void 소셜_식별자가_유효한_경우에는_바인딩이_생성되어야한다() {
    OAuthBinding binding = OAuthBinding.of(OAuthProvider.GOOGLE, "subject-123");
    assertEquals(OAuthProvider.GOOGLE, binding.provider());
    assertEquals("subject-123", binding.providerSubject());
  }

  @Test
  void 소셜_식별자가_비어있는_경우에는_생성이_실패해야한다() {
    assertThrows(IllegalArgumentException.class, () -> OAuthBinding.of(OAuthProvider.GOOGLE, " "));
  }
}
