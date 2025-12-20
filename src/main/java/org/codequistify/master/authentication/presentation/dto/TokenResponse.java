package org.codequistify.master.authentication.presentation.dto;

import org.codequistify.master.authentication.application.command.IssuedTokens;

public record TokenResponse(String accessToken, String refreshToken) {
  public static TokenResponse from(IssuedTokens tokens) {
    return new TokenResponse(tokens.accessToken(), tokens.refreshToken());
  }
}
