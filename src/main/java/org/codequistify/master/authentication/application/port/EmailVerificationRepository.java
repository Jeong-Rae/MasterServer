package org.codequistify.master.authentication.application.port;

import java.util.Optional;
import org.codequistify.master.authentication.domain.model.EmailVerification;
import org.codequistify.master.authentication.domain.model.EmailVerificationType;
import org.codequistify.master.authentication.domain.vo.Email;

public interface EmailVerificationRepository {
  Optional<EmailVerification> findByEmailAndType(Email email, EmailVerificationType type);

  void save(EmailVerification verification);
}
