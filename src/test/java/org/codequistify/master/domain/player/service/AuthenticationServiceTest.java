package org.codequistify.master.domain.player.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.codequistify.master.domain.authentication.dto.LogInRequest;
import org.codequistify.master.domain.authentication.dto.SignUpRequest;
import org.codequistify.master.domain.authentication.service.AuthenticationService;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class AuthenticationServiceTest {
    private final AuthenticationService authenticationService;
    private final PlayerDetailsService playerDetailsService;

    @Autowired
    public AuthenticationServiceTest(
            AuthenticationService authenticationService, PlayerDetailsService playerDetailsService) {
        this.authenticationService = authenticationService;
        this.playerDetailsService = playerDetailsService;
    }

    @BeforeEach
    void 테스트_데이터를_준비한다() {
        SignUpRequest request = new SignUpRequest("A", "A@pol.or.kr", "password99763892*");
        PlayerProfileResponse result = authenticationService.signUp(request);
    }

    @Test
    @Order(1)
    void 새로운_회원이_회원가입하면_프로필을_반환한다() {
        SignUpRequest request = new SignUpRequest("B", "B@pol.or.kr", "password99763892*");
        PlayerProfileResponse result = authenticationService.signUp(request);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(request.name());
        assertThat(result.email()).isEqualTo(request.email());
    }

    @Test
    void 회원가입하면_저장된_비밀번호는_비크립트_접두사를_가진다() {
        SignUpRequest request = new SignUpRequest("C", "C@pol.or.kr", "password99763892*");
        authenticationService.signUp(request);

        Player player = playerDetailsService.findOnePlayerByEmail(request.email());

        assertThat(player.authentication().hashedPassword().value()).startsWith("{bcrypt}");
    }

    @Test
    void 이미_가입된_이메일로_회원가입하면_중복_예외를_던진다() {
        SignUpRequest request = new SignUpRequest("A", "A@pol.or.kr", "password99763892*");

        assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
    }

    /*@Test
    void 이메일_양식이_맞지_않으면_예외를_던진다() {
        SignUpRequest request = new SignUpRequest("C", "", "password99763892*");

        assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }*/

    @Test
    void 비밀번호_조건이_맞지_않으면_정책_위반_예외를_던진다() {
        SignUpRequest request = new SignUpRequest("D", "D@pol.or.kr", "pass63892");

        assertThatThrownBy(() -> authenticationService.signUp(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PASSWORD_POLICY_VIOLATION.getMessage());
    }

    @Test
    void 가입된_이메일과_비밀번호로_로그인하면_프로필을_반환한다() {
        LogInRequest request = new LogInRequest("A@pol.or.kr", "password99763892*");

        PlayerProfileResponse result = authenticationService.logIn(request);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(request.email());
    }

    @Test
    void 잘못된_비밀번호로_로그인하면_인증_예외를_던진다() {
        LogInRequest request = new LogInRequest("A@pol.or.kr", "wrong");

        assertThatThrownBy(() -> authenticationService.logIn(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_EMAIL_OR_PASSWORD.getMessage());
    }

    @Test
    void 없는_이메일로_로그인하면_인증_예외를_던진다() {
        LogInRequest request = new LogInRequest("wrong@pol.or.kr", "password99763892*");

        assertThatThrownBy(() -> authenticationService.logIn(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_EMAIL_OR_PASSWORD.getMessage());
    }
}
