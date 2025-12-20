package org.codequistify.master.authentication.presentation.dto;

public record EmailDuplicationResponse(boolean available) {
  public static EmailDuplicationResponse of(boolean available) {
    return new EmailDuplicationResponse(available);
  }
}
