package org.codequistify.master.authentication.application.command;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.authentication.application.port.AuthenticationAccountRepository;
import org.codequistify.master.authentication.application.port.AuthorityQueryPort;
import org.codequistify.master.authentication.application.port.OAuthSubjectResolver;
import org.codequistify.master.authentication.application.port.PasswordHasher;
import org.codequistify.master.authentication.application.port.PlayerRegistrationPort;
import org.codequistify.master.authentication.application.port.SessionRepository;
import org.codequistify.master.authentication.application.port.TokenIssuer;
import org.codequistify.master.authentication.application.query.view.AuthenticationView;
import org.codequistify.master.authentication.application.query.view.AuthorizationView;
import org.codequistify.master.authentication.domain.model.AuthenticationAccount;
import org.codequistify.master.authentication.domain.model.OAuthProvider;
import org.codequistify.master.authentication.domain.model.Session;
import org.codequistify.master.authentication.domain.service.RegistrationDomainService;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.authentication.domain.vo.LocalCredential;
import org.codequistify.master.authentication.domain.vo.PasswordHash;
import org.codequistify.master.authentication.domain.vo.SessionId;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationCommandService {
  private final AuthenticationAccountRepository authenticationAccountRepository;
  private final SessionRepository sessionRepository;
  private final PasswordHasher passwordHasher;
  private final RegistrationDomainService registrationDomainService;
  private final OAuthSubjectResolver oAuthSubjectResolver;
  private final PlayerRegistrationPort playerRegistrationPort;
  private final AuthorityQueryPort authorityQueryPort;
  private final TokenIssuer tokenIssuer;

  @Transactional
  public AuthenticationView registerLocal(String emailValue, String password, String nickname) {
    ensureNicknameValid(nickname);
    Email email = Email.of(emailValue);
    ensureEmailAvailable(email);

    PlayerId playerId = PlayerId.generate();
    PasswordHash passwordHash = passwordHasher.hash(password);
    AuthenticationAccount account =
        registrationDomainService.registerLocal(playerId, email, passwordHash);

    authenticationAccountRepository.save(account);
    playerRegistrationPort.registerNewPlayer(playerId, nickname);

    AuthorizationView authorizationView = authorityQueryPort.getAuthorizationView(playerId);
    IssuedTokens tokens = issueNewTokens(playerId, authorizationView);

    return new AuthenticationView(playerId, tokens);
  }

  @Transactional
  public AuthenticationView loginLocal(String emailValue, String password) {
    Email email = Email.of(emailValue);
    AuthenticationAccount account = authenticationAccountRepository
        .findByEmail(email)
        .orElseThrow(() ->
            new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, HttpStatus.BAD_REQUEST));

    LocalCredential localCredential = account.credentials().localCredential();
    if (localCredential == null) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    if (!passwordHasher.matches(password, localCredential.passwordHash())) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    PlayerId playerId = account.playerId();
    AuthorizationView authorizationView = authorityQueryPort.getAuthorizationView(playerId);
    IssuedTokens tokens = issueNewTokens(playerId, authorizationView);

    return new AuthenticationView(playerId, tokens);
  }

  @Transactional
  public AuthenticationView loginOAuth(
      OAuthProvider provider, String authorizationCode, String nicknameCandidate) {
    ensureNicknameValid(nicknameCandidate);
    OAuthSubjectResolver.OAuthSubject subject =
        oAuthSubjectResolver.resolve(provider, authorizationCode);

    AuthenticationAccount account = authenticationAccountRepository
        .findByOAuth(provider, subject.subject())
        .orElseGet(() -> registerOAuthAccount(subject, provider, nicknameCandidate));

    PlayerId playerId = account.playerId();
    AuthorizationView authorizationView = authorityQueryPort.getAuthorizationView(playerId);
    IssuedTokens tokens = issueNewTokens(playerId, authorizationView);

    return new AuthenticationView(playerId, tokens);
  }

  @Transactional
  public IssuedTokens refreshToken(String refreshToken) {
    Session session = sessionRepository
        .findByRefreshToken(refreshToken)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED));

    AuthorizationView authorizationView =
        authorityQueryPort.getAuthorizationView(session.playerId());
    IssuedTokens tokens =
        tokenIssuer.reissueTokens(session.playerId(), authorizationView, session.sessionId());

    Session refreshed = Session.of(
        session.sessionId(),
        session.playerId(),
        tokens.refreshToken(),
        tokens.issuedAt(),
        tokens.refreshTokenExpiresAt());

    sessionRepository.save(refreshed);
    return tokens;
  }

  @Transactional
  public void logout(String refreshToken) {
    Session session = sessionRepository
        .findByRefreshToken(refreshToken)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.EXPIRED_REFRESH_TOKEN, HttpStatus.UNAUTHORIZED));
    sessionRepository.deleteBySessionId(session.sessionId());
  }

  private void ensureEmailAvailable(Email email) {
    if (authenticationAccountRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
    }
  }

  private AuthenticationAccount registerOAuthAccount(
      OAuthSubjectResolver.OAuthSubject subject, OAuthProvider provider, String nicknameCandidate) {
    PlayerId playerId = PlayerId.generate();
    AuthenticationAccount account = registrationDomainService.registerOAuth(
        playerId, subject.email(), provider, subject.subject());

    authenticationAccountRepository.save(account);

    playerRegistrationPort.registerNewPlayer(playerId, nicknameCandidate);

    return account;
  }

  private IssuedTokens issueNewTokens(PlayerId playerId, AuthorizationView authorizationView) {
    SessionId sessionId = SessionId.newId();
    IssuedTokens tokens = tokenIssuer.issueTokens(playerId, authorizationView, sessionId);
    Session session = Session.of(
        sessionId,
        playerId,
        tokens.refreshToken(),
        tokens.issuedAt(),
        tokens.refreshTokenExpiresAt());
    sessionRepository.save(session);
    return tokens;
  }

  private void ensureNicknameValid(String nickname) {
    if (nickname == null || nickname.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_NICKNAME, HttpStatus.BAD_REQUEST);
    }
  }
}
