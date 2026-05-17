package org.codequistify.master.domain.authentication.persistence;

import java.util.Optional;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationEntity, Long> {
    Optional<EmailVerificationEntity> findFirstByEmailAndUsedAndEmailVerificationTypeOrderByCreatedDateDesc(
            String email, Boolean used, EmailVerificationType type);
}
