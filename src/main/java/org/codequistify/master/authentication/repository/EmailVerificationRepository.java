package org.codequistify.master.domain.authentication.repository;

import java.util.Optional;
import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.shared.vo.EmailAddress;

public interface EmailVerificationRepository {
    EmailVerification save(EmailVerification emailVerification);

    Optional<EmailVerification> findLatestByEmailAndUsedAndType(
            EmailAddress email, Boolean used, EmailVerificationType type);
}
