package org.codequistify.master.authentication.presentation.dto;

public record EmailVerificationResponse(boolean verified) {
  public static EmailVerificationResponse of(boolean verified) {
    return new EmailVerificationResponse(verified);
  }
}
