package org.codequistify.master.domain.authentication.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.dto.*;
import org.codequistify.master.domain.authentication.service.AuthenticationService;
import org.codequistify.master.domain.authentication.service.EmailVerificationService;
import org.codequistify.master.domain.authentication.service.OAuthSignService;
import org.codequistify.master.domain.authentication.vo.OAuthCode;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.aspect.LogMethodInvocation;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.jwt.TokenIssuer;
import org.codequistify.master.global.jwt.TokenVerifier;
import org.codequistify.master.global.jwt.cookie.TokenCookieManager;
import org.codequistify.master.global.jwt.dto.TokenInfoResponse;
import org.codequistify.master.global.jwt.dto.TokenRequest;
import org.codequistify.master.global.jwt.dto.TokenResponse;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.IssuedTokens;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.codequistify.master.global.jwt.vo.TokenAudience;
import org.codequistify.master.global.jwt.vo.TokenAuthorities;
import org.codequistify.master.shared.response.BasicResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AuthenticationController implements AuthenticationApi {
    private final AuthenticationService authenticationService;
    private final OAuthSignService oAuthSignService;
    private final EmailVerificationService emailVerificationService;
    private final PlayerDetailsService playerDetailsService;

    private final TokenIssuer tokenIssuer;
    private final TokenVerifier tokenVerifier;
    private final TokenCookieManager tokenCookieManager;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Override
    @LogMonitoring
    @LogMethodInvocation
    @LogExecutionTime
    @GetMapping("auth/oauth-providers/{type}/authorization-url")
    public ResponseEntity<BasicResponse> getOAuthAuthorizationUrl(@PathVariable String type) {
        OAuthType oAuthType = OAuthType.from(type);
        BasicResponse response = BasicResponse.of(oAuthSignService.loginUrl(oAuthType));
        return ResponseEntity.ok(response);
    }

    @Override
    @LogMonitoring
    @PostMapping("auth/oauth-sessions/{type}")
    public ResponseEntity<LoginResponse> createOAuthSession(
            @PathVariable String type, @Valid @RequestBody SocialLogInRequest request, HttpServletResponse response) {
        OAuthType oAuthType = OAuthType.from(type);
        PlayerProfileResponse playerProfile = oAuthSignService.login(oAuthType, new OAuthCode(request.code()));
        IssuedTokens issuedTokens = issueTokens(playerProfile);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LoginResponse loginResponse = new LoginResponse(playerProfile, tokenResponse);

        LOGGER.info("[createOAuthSession] {} 로그인, Player: {}", oAuthType, playerProfile.id());
        return ResponseEntity.ok(loginResponse);
    }

    @Override
    @LogMonitoring
    @PostMapping("auth/registrations")
    public ResponseEntity<LoginResponse> createRegistration(
            @Valid @RequestBody SignUpRequest request, HttpServletResponse response) {
        // 이메일 인증 정보를 확인
        EmailVerification emailVerification =
                emailVerificationService.verifyEmailAndRetrieve(request.email(), EmailVerificationType.REGISTRATION);

        PlayerProfileResponse playerProfile = authenticationService.signUp(request);

        // 인증메일 사용처리
        emailVerificationService.markEmailVerificationAsUsed(emailVerification);

        IssuedTokens issuedTokens = issueTokens(playerProfile);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LoginResponse loginResponse = new LoginResponse(playerProfile, tokenResponse);

        LOGGER.info("[createRegistration] pol 회원가입, Player: {}", playerProfile.id());
        return ResponseEntity.ok(loginResponse);
    }

    @Override
    @LogMonitoring
    @PostMapping("auth/sessions")
    public ResponseEntity<LoginResponse> createSession(
            @Valid @RequestBody LogInRequest request, HttpServletResponse response) {
        PlayerProfileResponse playerProfile = authenticationService.logIn(request);

        IssuedTokens issuedTokens = issueTokens(playerProfile);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LoginResponse loginResponse = new LoginResponse(playerProfile, tokenResponse);

        LOGGER.info("[createSession] pol 로그인, Player: {}", playerProfile.id());
        return ResponseEntity.ok(loginResponse);
    }

    @Override
    @LogMonitoring
    @PostMapping("auth/access-tokens")
    public ResponseEntity<TokenResponse> createAccessToken(
            @Valid @RequestBody TokenRequest request, HttpServletResponse response) {
        IssuedTokens issuedTokens = authenticationService.reissueAccessToken(request);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LOGGER.info("[createAccessToken] AccessToken 재발급");
        return ResponseEntity.ok(tokenResponse);
    }

    @Override
    @LogMonitoring
    @GetMapping("auth/token-info")
    public ResponseEntity<TokenInfoResponse> getTokenInfo(@RequestParam String token) {
        try {
            TokenInfoResponse tokenInfo = authenticationService.analyzeTokenInfo(token);
            return ResponseEntity.ok(tokenInfo);
        } catch (NullPointerException exception) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @LogMonitoring
    @DeleteMapping("auth/sessions/me")
    public ResponseEntity<BasicResponse> deleteMySession(
            HttpServletRequest request, HttpServletResponse servletResponse) {
        String token = tokenVerifier.resolveToken(request);
        String aud = tokenVerifier.getAudience(token);
        Player player = playerDetailsService.findOnePlayerById(aud);
        if (player == null) {
            throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
        authenticationService.logOut(player);
        LOGGER.info("[deleteMySession] Player: {}, 로그아웃 완료", player.id().value());

        tokenCookieManager.expireTokenCookies(servletResponse);

        return ResponseEntity.ok(BasicResponse.of("SUCCESS"));
    }

    @Override
    @LogMonitoring
    @GetMapping("auth/emails/{email}")
    public ResponseEntity<BasicResponse> getEmailAvailability(@PathVariable String email) {
        if (authenticationService.checkEmailDuplication(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(BasicResponse.of("사용가능한 이메일입니다."));
        }
    }

    @Override
    @PostMapping("auth/email-verifications")
    @LogMonitoring
    public ResponseEntity<BasicResponse> createEmailVerification(@Valid @RequestBody EmailVerificationRequest request)
            throws MessagingException {
        LOGGER.info(request.type().toString());
        if (request.type() == EmailVerificationType.REGISTRATION) {
            if (authenticationService.checkEmailDuplication(request.email())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
            }
        }
        if (request.type() == EmailVerificationType.PASSWORD_RESET) {
            if (!authenticationService.checkEmailDuplication(request.email())) {
                throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        }

        emailVerificationService.sendVerifyMail(request.email(), request.type()); // 비동기 요청 : 메일 전송에 시간이 너무 오래걸림

        LOGGER.info("[createEmailVerification] {} 인증 메일 전송", request.email());
        BasicResponse response = new BasicResponse(ErrorCode.SUCCESS.getCode(), "", "");
        return ResponseEntity.ok(response);
    }

    @Override
    @LogMonitoring
    @GetMapping("auth/email-verifications")
    public ResponseEntity<BasicResponse> getEmailVerificationResult(
            @RequestParam String email, @RequestParam String code) {
        code = code.trim();
        String bool = Boolean.toString(emailVerificationService.verifyCode(email, code));

        LOGGER.info("[getEmailVerificationResult] {} 회원가입 메일 코드 인증 {}", email, bool);
        return ResponseEntity.ok(BasicResponse.of(bool));
    }

    @Override
    @LogMonitoring
    @PostMapping("auth/temp-access-tokens")
    public ResponseEntity<TokenResponse> createTempAccessToken(@Valid @RequestBody TempAccessTokenRequest request) {
        String email = request.email();
        EmailVerification emailVerification = emailVerificationService // 인증정보 없으면 서비스에서 예외
                .getEmailVerificationByEmail(email, false, EmailVerificationType.PASSWORD_RESET);

        emailVerificationService.markEmailVerificationAsUsed(emailVerification);

        AccessToken accessToken = tokenIssuer.issueTempToken(email);
        TokenResponse response = TokenResponse.from(IssuedTokens.accessOnly(accessToken));

        return ResponseEntity.ok(response);
    }

    private IssuedTokens issueTokens(PlayerProfileResponse playerProfile) {
        TokenAudience audience = TokenAudience.from(playerProfile.id());
        RefreshToken refreshToken = tokenIssuer.issueRefreshToken(new RefreshTokenClaims(audience));
        authenticationService.updateRefreshToken(playerProfile.id(), refreshToken.value()); // refresh token db에 저장

        AccessToken accessToken = tokenIssuer.issueAccessToken(
                new AccessTokenClaims(audience, TokenAuthorities.fromStoredAuthorities(playerProfile.roles())));

        return IssuedTokens.of(refreshToken, accessToken);
    }
}
