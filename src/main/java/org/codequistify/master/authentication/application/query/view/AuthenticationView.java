package org.codequistify.master.authentication.application.query.view;

import java.util.Objects;
import org.codequistify.master.authentication.application.command.IssuedTokens;
import org.codequistify.master.player.domain.vo.PlayerId;

public record AuthenticationView(PlayerId playerId, IssuedTokens tokens) {
  public AuthenticationView {
    Objects.requireNonNull(playerId, "playerId must not be null");
    Objects.requireNonNull(tokens, "tokens must not be null");
  }
}
