package org.codequistify.master.authentication.application.port;

import org.codequistify.master.authentication.domain.model.EmailVerificationType;
import org.codequistify.master.authentication.domain.vo.Email;

public interface VerificationEmailSender {
  void send(Email email, EmailVerificationType type, String code);
}
