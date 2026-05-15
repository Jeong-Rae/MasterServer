package org.codequistify.master.domain.authentication.persistence;

import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationJpaRepository extends JpaRepository<EmailVerificationEntity, Long> {
    Optional<EmailVerificationEntity> findFirstByEmailAndUsedAndEmailVerificationTypeOrderByCreatedDateDesc(
            String email,
            Boolean used,
            EmailVerificationType type);
}
