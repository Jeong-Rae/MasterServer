package org.codequistify.master.domain.authentication.repository;

import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.shared.vo.EmailAddress;

import java.util.Optional;

public interface EmailVerificationRepository {
    EmailVerification save(EmailVerification emailVerification);

    Optional<EmailVerification> findLatestByEmailAndUsedAndType(
            EmailAddress email,
            Boolean used,
            EmailVerificationType type);
}
