package org.codequistify.master.authentication.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.authentication.application.command.EmailVerificationCommandService;
import org.codequistify.master.authentication.presentation.dto.EmailVerificationConfirmRequest;
import org.codequistify.master.authentication.presentation.dto.EmailVerificationRequest;
import org.codequistify.master.authentication.presentation.dto.EmailVerificationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/email")
public class EmailVerificationController {
  private final EmailVerificationCommandService emailVerificationCommandService;

  @PostMapping("/verification")
  public ResponseEntity<Void> sendVerification(
      @Valid @RequestBody EmailVerificationRequest request) {
    emailVerificationCommandService.sendVerificationMail(request.email(), request.type());
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/verify")
  public ResponseEntity<EmailVerificationResponse> verify(
      @Valid @RequestBody EmailVerificationConfirmRequest request) {
    emailVerificationCommandService.verify(request.email(), request.code(), request.type());
    return ResponseEntity.status(HttpStatus.OK).body(EmailVerificationResponse.of(true));
  }
}
