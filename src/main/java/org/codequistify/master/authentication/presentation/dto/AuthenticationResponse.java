package org.codequistify.master.authentication.presentation.dto;

import java.util.UUID;
import org.codequistify.master.authentication.application.query.view.AuthenticationView;

public record AuthenticationResponse(UUID playerId, TokenResponse token) {
  public static AuthenticationResponse from(AuthenticationView view) {
    return new AuthenticationResponse(view.playerId().value(), TokenResponse.from(view.tokens()));
  }
}
