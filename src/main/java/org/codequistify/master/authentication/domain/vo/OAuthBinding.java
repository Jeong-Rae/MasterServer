package org.codequistify.master.authentication.domain.vo;

import java.time.Instant;
import java.util.Objects;
import org.codequistify.master.authentication.domain.model.OAuthProvider;

public record OAuthBinding(OAuthProvider provider, String providerSubject, Instant linkedAt) {
  public OAuthBinding {
    Objects.requireNonNull(provider, "provider must not be null");
    Objects.requireNonNull(providerSubject, "providerSubject must not be null");
    if (providerSubject.isBlank()) {
      throw new IllegalArgumentException("providerSubject must not be blank");
    }
  }

  public static OAuthBinding of(OAuthProvider provider, String providerSubject) {
    return new OAuthBinding(provider, providerSubject, null);
  }

  public static OAuthBinding of(OAuthProvider provider, String providerSubject, Instant linkedAt) {
    return new OAuthBinding(provider, providerSubject, linkedAt);
  }
}
