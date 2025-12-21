package org.codequistify.master.authentication.application.command;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.authentication.application.port.AuthenticationAccountRepository;
import org.codequistify.master.authentication.application.port.EmailVerificationRepository;
import org.codequistify.master.authentication.application.port.VerificationCodeGenerator;
import org.codequistify.master.authentication.application.port.VerificationEmailSender;
import org.codequistify.master.authentication.domain.model.EmailVerification;
import org.codequistify.master.authentication.domain.model.EmailVerificationType;
import org.codequistify.master.authentication.domain.vo.Email;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
  private static final String EMPTY_CODE = "";

  private final EmailVerificationRepository emailVerificationRepository;
  private final AuthenticationAccountRepository authenticationAccountRepository;
  private final VerificationCodeGenerator verificationCodeGenerator;
  private final VerificationEmailSender verificationEmailSender;

  @Transactional
  public void sendVerificationMail(String emailValue, EmailVerificationType type) {
    Email email = Email.of(emailValue);
    validateEmailByType(email, type);

    String code = verificationCodeGenerator.generate();
    EmailVerification verification = EmailVerification.of(email, code, type);
    emailVerificationRepository.save(verification);
    verificationEmailSender.send(email, type, code);
  }

  @Transactional
  public void verify(String emailValue, String codeValue, EmailVerificationType type) {
    Email email = Email.of(emailValue);
    EmailVerification verification = emailVerificationRepository
        .findByEmailAndType(email, type)
        .orElseThrow(
            () -> new BusinessException(ErrorCode.EMAIL_VERIFIED_FAILURE, HttpStatus.BAD_REQUEST));

    String code = codeValue == null ? EMPTY_CODE : codeValue.trim();
    if (!verification.code().equals(code)) {
      throw new BusinessException(ErrorCode.EMAIL_VERIFIED_FAILURE, HttpStatus.BAD_REQUEST);
    }

    verification.verify();
    verification.markAsUsed();
    emailVerificationRepository.save(verification);
  }

  public void ensureRegistrationEmailAvailable(String emailValue) {
    Email email = Email.of(emailValue);
    validateEmailByType(email, EmailVerificationType.REGISTRATION);
  }

  private void validateEmailByType(Email email, EmailVerificationType type) {
    if (type == EmailVerificationType.REGISTRATION
        && authenticationAccountRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
    }
    if (type == EmailVerificationType.PASSWORD_RESET
        && !authenticationAccountRepository.existsByEmail(email)) {
      throw new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
  }
}
