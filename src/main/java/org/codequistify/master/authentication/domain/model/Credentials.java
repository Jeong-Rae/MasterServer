package org.codequistify.master.authentication.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.codequistify.master.authentication.domain.vo.LocalCredential;
import org.codequistify.master.authentication.domain.vo.OAuthBinding;

public final class Credentials {
  private LocalCredential localCredential;
  private final Set<OAuthBinding> oauthBindings;

  private Credentials(LocalCredential localCredential, Set<OAuthBinding> oauthBindings) {
    this.localCredential = localCredential;
    if (oauthBindings == null || oauthBindings.isEmpty()) {
      this.oauthBindings = new HashSet<>();
    } else {
      this.oauthBindings = new HashSet<>(oauthBindings);
    }
  }

  public static Credentials of(LocalCredential localCredential, Set<OAuthBinding> oauthBindings) {
    return new Credentials(localCredential, oauthBindings);
  }

  public static Credentials withLocal(LocalCredential localCredential) {
    Objects.requireNonNull(localCredential, "localCredential must not be null");
    return new Credentials(localCredential, null);
  }

  public LocalCredential localCredential() {
    return localCredential;
  }

  public void setLocalCredential(LocalCredential localCredential) {
    this.localCredential =
        Objects.requireNonNull(localCredential, "localCredential must not be null");
  }

  public Set<OAuthBinding> oauthBindings() {
    return Collections.unmodifiableSet(oauthBindings);
  }

  public void addOAuthBinding(OAuthBinding binding) {
    oauthBindings.add(Objects.requireNonNull(binding, "binding must not be null"));
  }
}
