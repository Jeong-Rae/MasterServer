package org.codequistify.master.domain.authentication.service;

import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.mail.VerificationMailRequest;
import org.codequistify.master.domain.authentication.mail.VerificationMailSender;
import org.codequistify.master.domain.authentication.repository.EmailVerificationRepository;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationRepository emailVerificationRepository;

    @Mock
    private VerificationMailSender verificationMailSender;

    private EmailVerificationService emailVerificationService;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        emailVerificationService = new EmailVerificationService(emailVerificationRepository, verificationMailSender);
        ReflectionTestUtils.setField(emailVerificationService, "mailSecret", "mail-secret");
    }

    @Test
    void 인증메일_전송에_성공하면_인증정보를_저장한다() {
        emailVerificationService.sendVerifyMail("pol@example.com", EmailVerificationType.REGISTRATION);

        ArgumentCaptor<VerificationMailRequest> mailCaptor = ArgumentCaptor.forClass(VerificationMailRequest.class);
        ArgumentCaptor<EmailVerification> verificationCaptor = ArgumentCaptor.forClass(EmailVerification.class);
        verify(verificationMailSender).send(mailCaptor.capture());
        verify(emailVerificationRepository).save(verificationCaptor.capture());
        assertThat(mailCaptor.getValue().to().value()).isEqualTo("pol@example.com");
        assertThat(mailCaptor.getValue().type()).isEqualTo(EmailVerificationType.REGISTRATION);
        assertThat(mailCaptor.getValue().code().value()).isNotBlank();
        assertThat(verificationCaptor.getValue().email().value()).isEqualTo("pol@example.com");
        assertThat(verificationCaptor.getValue().code()).isEqualTo(mailCaptor.getValue().code());
    }

    @Test
    void Email과_Code가_같은_규칙으로_생성되면_인증_Code가_일치한다() throws Exception {
        String issuedCode = sha256("pol@example.com" + "mail-secret");

        boolean result = emailVerificationService.verifyCode("pol@example.com", issuedCode);

        assertThat(result).isTrue();
    }

    @Test
    void Email과_Code가_다른_규칙이면_인증_Code가_일치하지_않는다() {
        boolean result = emailVerificationService.verifyCode("pol@example.com", "wrong-code");

        assertThat(result).isFalse();
    }

    @Test
    void 인증메일_전송에_실패하면_EMAIL_SENDING_FAILURE를_던지고_저장하지_않는다() {
        doThrow(new MailSendException("fail")).when(verificationMailSender).send(any(VerificationMailRequest.class));

        assertThatThrownBy(() -> emailVerificationService.sendVerifyMail("pol@example.com", EmailVerificationType.REGISTRATION))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMAIL_SENDING_FAILURE);
        verify(emailVerificationRepository, never()).save(any(EmailVerification.class));
    }

    private String sha256(String value) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(value.getBytes(StandardCharsets.UTF_8));
        return String.format("%064x", new BigInteger(1, messageDigest.digest()));
    }
}
