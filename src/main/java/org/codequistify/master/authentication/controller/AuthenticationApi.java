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
import org.codequistify.master.domain.authentication.dto.EmailVerificationRequest;
import org.codequistify.master.domain.authentication.dto.LogInRequest;
import org.codequistify.master.domain.authentication.dto.LoginResponse;
import org.codequistify.master.domain.authentication.dto.SignUpRequest;
import org.codequistify.master.domain.authentication.dto.SocialLogInRequest;
import org.codequistify.master.domain.authentication.dto.TempAccessTokenRequest;
import org.codequistify.master.global.jwt.dto.TokenInfoResponse;
import org.codequistify.master.global.jwt.dto.TokenRequest;
import org.codequistify.master.global.jwt.dto.TokenResponse;
import org.codequistify.master.shared.response.BasicResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Authentication")
public interface AuthenticationApi {

    @Operation(
            summary = "OAuth 로그인 url 발급",
            description = "OAuth provider 로그인 화면으로 넘어갈 수 있는 url을 발급한다. 고정값이며 저장해여 사용할 수 있다.")
    ResponseEntity<BasicResponse> getOAuthAuthorizationUrl(@PathVariable String type);

    @Operation(
            summary = "OAuth 로그인 요청",
            description = "redirect로 받은 code와 provider type을 인자로 전달한다. 유효한 code라면 사용자 profile과 인증 토큰을 발급받는다.")
    ResponseEntity<LoginResponse> createOAuthSession(
            @PathVariable String type, @Valid @RequestBody SocialLogInRequest request, HttpServletResponse response);

    @Operation(summary = "POL 자체 회원가입", description = """
        자체 회원가입이다. name, email, password를 필수로 입력받는다.

        에러 예시
        - 메일 인증 실패 : `"error": "4402_EMAIL_VERIFIED_FAILURE_ERROR"`
        - 메일 중복 : `"error": "4201_EMAIL_ALREADY_EXISTS_ERROR"`
        - 다른 소셜 로그인 방식으로 가입: `"error": "4202_ EMAIL_ALREADY_EXISTS_OTHER_AUTH_ERROR`
        - 비밀번호 조건 미충족: `"error": "4103_PASSWORD_POLICY_VIOLATION_ERROR"`
        - 중복된 이름: `"error": "4106_DUPLICATE_NAME_ERROR"`
        - 이름에 비속어 포함: `"error": "4105_PROFANITY_IN_NAME_ERROR"`
        """)
    ResponseEntity<LoginResponse> createRegistration(
            @Valid @RequestBody SignUpRequest request, HttpServletResponse response);

    @Operation(summary = "POL 자체 로그인", description = "자체 로그인기능이다. name, password를 필수로 입력받는다.")
    ResponseEntity<LoginResponse> createSession(@Valid @RequestBody LogInRequest request, HttpServletResponse response);

    @Operation(
            summary = "엑세스 토큰 재발급",
            description = """
                AccessToken을 재발급 한다.
                RefreshToken 값을 body로 받는다. 이때 token은 'Bearer' 없이 token 값만을 적어야 한다.\
                """,
            responses = {
                @ApiResponse(responseCode = "200", description = "정상적으로 재발급"),
                @ApiResponse(responseCode = "400", description = "잘못된 요청, id 불일치 또는 빈 요청 등"),
                @ApiResponse(responseCode = "401", description = "만료된 Refresh Token")
            })
    ResponseEntity<TokenResponse> createAccessToken(
            @Valid @RequestBody TokenRequest request, HttpServletResponse response);

    @Operation(
            summary = "토큰 해석",
            description = "토큰 정보를 해석한다.",
            responses = {
                @ApiResponse(responseCode = "200", description = "유효한 토큰"),
                @ApiResponse(responseCode = "400", description = "잘못된 양식의 토큰 또는 셔명되지 않은 토큰 "),
            })
    ResponseEntity<TokenInfoResponse> getTokenInfo(@RequestParam String token);

    @Operation(
            summary = "로그아웃 요청",
            description = "Authorization에 토큰이 있어야 한다.",
            responses = {
                @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
                @ApiResponse(responseCode = "401", description = "잘못된 토큰 정보")
            })
    ResponseEntity<BasicResponse> deleteMySession(HttpServletRequest request, HttpServletResponse servletResponse);

    @Operation(
            summary = "이메일 중복 검사",
            description = "이메일 중복 검사이다. 등록되어 있는지 여부를 확인한다. 사용할 수 없으면 error에 내용이 담긴다.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "사용가능한 이메일",
                        content = @Content(schema = @Schema(implementation = BasicResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "사용할 수 없는 이메일",
                        content = @Content(schema = @Schema(implementation = BasicResponse.class)))
            })
    ResponseEntity<BasicResponse> getEmailAvailability(@PathVariable String email);

    @Operation(summary = "인증메일 발송 요청", description = """
        회원가입 인증메일을 발송하는 요청이다. 중복된 이메일인지 함께 검증한다.
        type 목록은 다음과 같다.
        - 회원가입 : *'REGISTRATION'*
        - 비밀번호 초기화 : *'PASSWORD_RESET'*\
        """)
    ResponseEntity<BasicResponse> createEmailVerification(@Valid @RequestBody EmailVerificationRequest request)
            throws MessagingException;

    @Operation(
            hidden = true,
            summary = "회원가입 인증코드 확인",
            description = "회원가입 인증코드를 확인한다. 사용자의 email과 입력 코드를 query로 받는다. 결과는 response 필드에 \"true\", \"false\"로 반환한다.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "인증 결과",
                        content = @Content(schema = @Schema(implementation = BasicResponse.class)))
            })
    ResponseEntity<BasicResponse> getEmailVerificationResult(@RequestParam String email, @RequestParam String code);

    @Operation(
            summary = "임시 엑세스 토큰 발급",
            description = "비밀번호 찾기를 위한 임시 엑세스 토큰을 발급한다.",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "인증 성공",
                        content = @Content(schema = @Schema(implementation = TokenResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "인증 실패",
                        content = @Content(schema = @Schema(implementation = BasicResponse.class)))
            })
    ResponseEntity<TokenResponse> createTempAccessToken(@Valid @RequestBody TempAccessTokenRequest request);
}
