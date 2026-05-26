package org.codequistify.master.domain.authentication.service;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.mail.VerificationMailRequest;
import org.codequistify.master.domain.authentication.mail.VerificationMailSender;
import org.codequistify.master.domain.authentication.repository.EmailVerificationRepository;
import org.codequistify.master.domain.authentication.vo.MailSecret;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.shared.vo.EmailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${mail.secret}")
    private String mailSecret;

    private final EmailVerificationRepository emailVerificationRepository;
    private final VerificationMailSender verificationMailSender;
    private final Logger LOGGER = LoggerFactory.getLogger(EmailVerificationService.class);

    @Async
    @Transactional
    public void sendVerifyMail(String email, EmailVerificationType emailVerificationType) {
        try {
            EmailAddress emailAddress = new EmailAddress(email);
            VerificationCode authCode = VerificationCode.issue(emailAddress, mailSecret());
            verificationMailSender.send(new VerificationMailRequest(emailAddress, authCode, emailVerificationType));
            LOGGER.info("[sendVerifyMail] {}로 메일 전송 완료", email);

            emailVerificationRepository.save(EmailVerification.issue(emailAddress, authCode, emailVerificationType));

        } catch (IllegalArgumentException | IllegalStateException | MailException exception) {
            LOGGER.info("[sendVerifyMail] {}로 메일 전송 실패", email);
            throw new BusinessException(ErrorCode.EMAIL_SENDING_FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 인증번호 비교
    @LogMonitoring
    public boolean verifyCode(String email, String code) {
        try {
            VerificationCode issuedCode = VerificationCode.issue(new EmailAddress(email), mailSecret());
            return issuedCode.matches(new VerificationCode(code));
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return false;
        }
    }

    // 메일 인증번호 확인
    @LogMonitoring
    @Transactional
    public void updateVerification(String email, EmailVerificationType type) {
        emailVerificationRepository
                .findLatestByEmailAndUsedAndType(new EmailAddress(email), false, type)
                .map(EmailVerification::verify)
                .ifPresent(emailVerificationRepository::save);
    }

    // 이메일 인증이 되어있는지 확인
    @Transactional(readOnly = true)
    public EmailVerification verifyEmailAndRetrieve(String email, EmailVerificationType type) {
        EmailVerification emailVerification = this.getEmailVerificationByEmail(email, false, type);

        // 이메일 인증이 되어 있는 계정인지 확인
        if (!emailVerification.isVerified()) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFIED_FAILURE, HttpStatus.BAD_REQUEST);
        }

        return emailVerification;
    }

    // 인증정보 가져오기
    @LogMonitoring
    @Transactional(readOnly = true)
    public EmailVerification getEmailVerificationByEmail(String email, Boolean used, EmailVerificationType type) {
        return emailVerificationRepository
                .findLatestByEmailAndUsedAndType(new EmailAddress(email), used, type)
                .orElseThrow(() -> {
                    LOGGER.info("[getEmailVerificationByEmail] {}에 대한 미사용 메일 인증 정보 없음", email);
                    return new BusinessException(ErrorCode.EMAIL_VERIFIED_FAILURE, HttpStatus.BAD_REQUEST);
                });
    }

    // 인증 정보 사용 기록
    @LogMonitoring
    @Transactional
    public void markEmailVerificationAsUsed(EmailVerification emailVerification) {
        emailVerificationRepository.save(emailVerification.markAsUsed());
    }

    private MailSecret mailSecret() {
        return new MailSecret(mailSecret);
    }
}
