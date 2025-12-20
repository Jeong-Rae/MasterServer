package org.codequistify.master.authentication.application.query.view;

import java.util.List;
import java.util.Objects;

public record AuthorizationView(List<String> roles, List<String> permissions) {
  public AuthorizationView {
    Objects.requireNonNull(roles, "roles must not be null");
    Objects.requireNonNull(permissions, "permissions must not be null");
  }
}
