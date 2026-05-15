package org.codequistify.master.domain.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.codequistify.master.domain.player.dto.PlayerProfile;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.aspect.LogMethodInvocation;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.jwt.TokenIssuer;
import org.codequistify.master.global.jwt.TokenVerifier;
import org.codequistify.master.global.jwt.cookie.TokenCookieManager;
import org.codequistify.master.global.jwt.dto.TokenInfo;
import org.codequistify.master.global.jwt.dto.TokenRequest;
import org.codequistify.master.global.jwt.dto.TokenResponse;
import org.codequistify.master.global.jwt.vo.AccessToken;
import org.codequistify.master.global.jwt.vo.AccessTokenClaims;
import org.codequistify.master.global.jwt.vo.IssuedTokens;
import org.codequistify.master.global.jwt.vo.RefreshTokenClaims;
import org.codequistify.master.global.jwt.vo.TokenAudience;
import org.codequistify.master.global.jwt.vo.TokenAuthorities;
import org.codequistify.master.global.util.BasicResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping("api")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final OAuthSignService oAuthSignService;
    private final EmailVerificationService emailVerificationService;
    private final PlayerDetailsService playerDetailsService;

    private final TokenIssuer tokenIssuer;
    private final TokenVerifier tokenVerifier;
    private final TokenCookieManager tokenCookieManager;
    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @Operation(
            summary = "OAuth 로그인 url 발급",
            description = "OAuth provider 로그인 화면으로 넘어갈 수 있는 url을 발급한다. 고정값이며 저장해여 사용할 수 있다."
    )
    @LogMonitoring
    @LogMethodInvocation
    @LogExecutionTime
    @GetMapping("auth/oauth/{type}/url")
    public ResponseEntity<BasicResponse> loginUrlOAuth(@PathVariable String type) {
        OAuthType oAuthType = OAuthType.from(type);
        BasicResponse response = BasicResponse.of(oAuthSignService.loginUrl(oAuthType));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "OAuth 로그인 요청",
            description = "redirect로 받은 code와 provider type을 인자로 전달한다. 유효한 code라면 사용자 profile과 인증 토큰을 발급받는다."
    )
    @LogMonitoring
    @PostMapping("auth/oauth/{type}")
    public ResponseEntity<LoginResponse> socialLogInOAuth(
            @PathVariable String type,
            @Valid @RequestBody SocialLogInRequest request,
            HttpServletResponse response
    ) {
        OAuthType oAuthType = OAuthType.from(type);
        PlayerProfile playerProfile = oAuthSignService.login(oAuthType, new OAuthCode(request.code()));
        IssuedTokens issuedTokens = issueTokens(playerProfile);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LoginResponse loginResponse = new LoginResponse(playerProfile, tokenResponse);

        LOGGER.info("[socialLogInOAuth] {} 로그인, Player: {}", oAuthType, playerProfile.id());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "POL 자체 회원가입",
            description = """
                    자체 회원가입이다. name, email, password를 필수로 입력받는다.

                    에러 예시
                    - 메일 인증 실패 : `"error": "4402_EMAIL_VERIFIED_FAILURE_ERROR"`
                    - 메일 중복 : `"error": "4201_EMAIL_ALREADY_EXISTS_ERROR"`
                    - 다른 소셜 로그인 방식으로 가입: `"error": "4202_ EMAIL_ALREADY_EXISTS_OTHER_AUTH_ERROR`
                    - 비밀번호 조건 미충족: `"error": "4103_PASSWORD_POLICY_VIOLATION_ERROR"`
                    - 중복된 이름: `"error": "4106_DUPLICATE_NAME_ERROR"`
                    - 이름에 비속어 포함: `"error": "4105_PROFANITY_IN_NAME_ERROR"`
                    """
    )
    @LogMonitoring
    @PostMapping("/auth/signup")
    public ResponseEntity<LoginResponse> signUpPOL(@Valid @RequestBody SignUpRequest request, HttpServletResponse response) {
        // 이메일 인증 정보를 확인
        EmailVerification emailVerification = emailVerificationService
                .verifyEmailAndRetrieve(request.email(), EmailVerificationType.REGISTRATION);

        PlayerProfile playerProfile = authenticationService.signUp(request);

        // 인증메일 사용처리
        emailVerificationService.markEmailVerificationAsUsed(emailVerification);

        IssuedTokens issuedTokens = issueTokens(playerProfile);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LoginResponse loginResponse = new LoginResponse(playerProfile, tokenResponse);

        LOGGER.info("[SignUpPOL] pol 회원가입, Player: {}", playerProfile.id());
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @Operation(
            summary = "POL 자체 로그인",
            description = "자체 로그인기능이다. name, password를 필수로 입력받는다."
    )
    @LogMonitoring
    @PostMapping("auth/login")
    public ResponseEntity<LoginResponse> logInPOL(@Valid @RequestBody LogInRequest request, HttpServletResponse response) {
        PlayerProfile playerProfile = authenticationService.logIn(request);

        IssuedTokens issuedTokens = issueTokens(playerProfile);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LoginResponse loginResponse = new LoginResponse(playerProfile, tokenResponse);

        LOGGER.info("[LogInPOL] pol 로그인, Player: {}", playerProfile.id());
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @Operation(
            summary = "엑세스 토큰 재발급",
            description = """
                    AccessToken을 재발급 한다.
                    RefreshToken 값을 body로 받는다. 이때 token은 'Bearer' 없이 token 값만을 적어야 한다.""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "정상적으로 재발급"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청, id 불일치 또는 빈 요청 등"),
                    @ApiResponse(responseCode = "401", description = "만료된 Refresh Token")
            }
    )
    @LogMonitoring
    @PostMapping("auth/refresh")
    public ResponseEntity<TokenResponse> reissueAccessToken(@RequestBody TokenRequest request, HttpServletResponse response) {
        IssuedTokens issuedTokens = authenticationService.reissueAccessToken(request);
        tokenCookieManager.addTokenCookies(response, issuedTokens);
        TokenResponse tokenResponse = TokenResponse.from(issuedTokens);

        LOGGER.info("[reissueAccessToken] AccessToken 재발급");
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    @Operation(
            summary = "토큰 해석",
            description = "토큰 정보를 해석한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유효한 토큰"),
                    @ApiResponse(responseCode = "400", description = "잘못된 양식의 토큰 또는 셔명되지 않은 토큰 "),
            }
    )
    @LogMonitoring
    @GetMapping("auth/validate")
    public ResponseEntity<TokenInfo> analyzeToken(@RequestParam String token) {
        try {
            TokenInfo tokenInfo = authenticationService.analyzeTokenInfo(token);
            return ResponseEntity.status(HttpStatus.OK).body(tokenInfo);
        } catch (NullPointerException exception) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "로그아웃 요청",
            description = "Authorization에 토큰이 있어야 한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "401", description = "잘못된 토큰 정보")
            }
    )
    @LogMonitoring
    @PostMapping("auth/logout")
    public ResponseEntity<BasicResponse> LogOut(HttpServletRequest request, HttpServletResponse servletResponse) {
        String token = tokenVerifier.resolveToken(request);
        String aud = tokenVerifier.getAudience(token);
        Player player = playerDetailsService.findOnePlayerById(aud);
        if (player == null) {
            throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
        }
        authenticationService.logOut(player);
        LOGGER.info("[LogOut] Player: {}, 로그아웃 완료", player.id().value());

        tokenCookieManager.expireTokenCookies(servletResponse);

        return ResponseEntity.status(HttpStatus.OK)
                .body(BasicResponse.of("SUCCESS"));
    }

    @Operation(
            summary = "이메일 중복 검사",
            description = "이메일 중복 검사이다. 등록되어 있는지 여부를 확인한다. 사용할 수 없으면 error에 내용이 담긴다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용가능한 이메일",
                            content = @Content(
                                    schema = @Schema(implementation = BasicResponse.class))),
                    @ApiResponse(responseCode = "400", description = "사용할 수 없는 이메일",
                            content = @Content(
                                    schema = @Schema(implementation = BasicResponse.class)))}
    )
    @LogMonitoring
    @GetMapping("auth/email/{email}")
    public ResponseEntity<BasicResponse> checkEmailDuplication(@PathVariable String email) {
        if (authenticationService.checkEmailDuplication(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BasicResponse.of("사용가능한 이메일입니다."));
        }
    }

    @Operation(
            summary = "인증메일 발송 요청",
            description = """
                    회원가입 인증메일을 발송하는 요청이다. 중복된 이메일인지 함께 검증한다.
                    type 목록은 다음과 같다.
                    - 회원가입 : *'REGISTRATION'*
                    - 비밀번호 초기화 : *'PASSWORD_RESET'*"""
    )
    @PostMapping("auth/email/verification")
    @LogMonitoring
    public ResponseEntity<BasicResponse> sendAuthMail(@Valid @RequestBody EmailVerificationRequest request) throws MessagingException {
        LOGGER.info(request.type().toString());
        if (request.type() == EmailVerificationType.REGISTRATION){
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

        LOGGER.info("[sendAuthMail] {} 인증 메일 전송", request.email());
        BasicResponse response = new BasicResponse(ErrorCode.SUCCESS.getCode(), "", "");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(
            hidden = true,
            summary = "회원가입 인증코드 확인",
            description = "회원가입 인증코드를 확인한다. 사용자의 email과 입력 코드를 path로 받는다. 결과는 response 필드에 \"true\", \"false\"로 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 결과", content = @Content(
                                    schema = @Schema(implementation = BasicResponse.class)))}
    )
    @LogMonitoring
    @GetMapping("auth/email/{email}/code/{code}")
    public ResponseEntity<BasicResponse> verifyCode(@PathVariable String email, @PathVariable String code) {
        code = code.trim();
        String bool = Boolean.toString(emailVerificationService.verifyCode(email, code));

        LOGGER.info("[verifyCode] {} 회원가입 메일 코드 인증 {}", email, bool);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BasicResponse.of(bool));
    }

    @Operation(
            summary = "임시 엑세스 토큰 발급",
            description = "비밀번호 찾기를 위한 임시 엑세스 토큰을 발급한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 성공",
                            content = @Content(
                                    schema = @Schema(implementation = TokenResponse.class))),
                    @ApiResponse(responseCode = "400", description = "인증 실패",
                            content = @Content(
                                    schema = @Schema(implementation = BasicResponse.class)))}
    )
    @LogMonitoring
    @GetMapping("/auth/access/{email}/temp")
    public ResponseEntity<TokenResponse> issueTempToken(@PathVariable String email) {
        EmailVerification emailVerification = emailVerificationService // 인증정보 없으면 서비스에서 예외
                .getEmailVerificationByEmail(email, false, EmailVerificationType.PASSWORD_RESET);

        emailVerificationService.markEmailVerificationAsUsed(emailVerification);

        AccessToken accessToken = tokenIssuer.issueTempToken(email);
        TokenResponse response = TokenResponse.from(IssuedTokens.accessOnly(accessToken));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    private IssuedTokens issueTokens(PlayerProfile playerProfile) {
        TokenAudience audience = TokenAudience.from(playerProfile.id());
        RefreshToken refreshToken = tokenIssuer.issueRefreshToken(new RefreshTokenClaims(audience));
        authenticationService.updateRefreshToken(playerProfile.id(), refreshToken.value()); // refresh token db에 저장

        AccessToken accessToken = tokenIssuer.issueAccessToken(new AccessTokenClaims(
                audience,
                TokenAuthorities.fromStoredAuthorities(playerProfile.roles())));

        return IssuedTokens.of(refreshToken, accessToken);
    }
}
