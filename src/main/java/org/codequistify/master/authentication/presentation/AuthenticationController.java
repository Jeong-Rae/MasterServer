package org.codequistify.master.authentication.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.authentication.application.command.AuthenticationCommandService;
import org.codequistify.master.authentication.application.command.IssuedTokens;
import org.codequistify.master.authentication.application.query.AuthenticationQueryService;
import org.codequistify.master.authentication.application.query.view.AuthenticationView;
import org.codequistify.master.authentication.presentation.dto.AuthenticationResponse;
import org.codequistify.master.authentication.presentation.dto.EmailDuplicationResponse;
import org.codequistify.master.authentication.presentation.dto.LoginRequest;
import org.codequistify.master.authentication.presentation.dto.LogoutRequest;
import org.codequistify.master.authentication.presentation.dto.OAuthLoginRequest;
import org.codequistify.master.authentication.presentation.dto.SignUpRequest;
import org.codequistify.master.authentication.presentation.dto.TokenRefreshRequest;
import org.codequistify.master.authentication.presentation.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
  private final AuthenticationCommandService authenticationCommandService;
  private final AuthenticationQueryService authenticationQueryService;

  @PostMapping("/signup")
  public ResponseEntity<AuthenticationResponse> signUp(@Valid @RequestBody SignUpRequest request) {
    AuthenticationView view = authenticationCommandService.registerLocal(
        request.email(), request.password(), request.nickname());
    return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.from(view));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthenticationView view =
        authenticationCommandService.loginLocal(request.email(), request.password());
    return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.from(view));
  }

  @PostMapping("/oauth")
  public ResponseEntity<AuthenticationResponse> loginOAuth(
      @Valid @RequestBody OAuthLoginRequest request) {
    AuthenticationView view = authenticationCommandService.loginOAuth(
        request.provider(), request.authorizationCode(), request.nicknameCandidate());
    return ResponseEntity.status(HttpStatus.OK).body(AuthenticationResponse.from(view));
  }

  @PostMapping("/token/refresh")
  public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
    IssuedTokens tokens = authenticationCommandService.refreshToken(request.refreshToken());
    return ResponseEntity.status(HttpStatus.OK).body(TokenResponse.from(tokens));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
    authenticationCommandService.logout(request.refreshToken());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/email/{email}/available")
  public ResponseEntity<EmailDuplicationResponse> checkEmail(@PathVariable String email) {
    boolean available = authenticationQueryService.isEmailAvailable(email);
    return ResponseEntity.status(HttpStatus.OK).body(EmailDuplicationResponse.of(available));
  }
}
