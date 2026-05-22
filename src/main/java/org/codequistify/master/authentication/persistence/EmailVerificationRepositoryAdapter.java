package org.codequistify.master.domain.authentication.persistence;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.repository.EmailVerificationRepository;
import org.codequistify.master.shared.vo.EmailAddress;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmailVerificationRepositoryAdapter implements EmailVerificationRepository {
    private final EmailVerificationJpaRepository emailVerificationJpaRepository;
    private final EmailVerificationPersistenceMapper emailVerificationPersistenceMapper;

    @Override
    public EmailVerification save(EmailVerification emailVerification) {
        EmailVerificationEntity saved =
                emailVerificationJpaRepository.save(emailVerificationPersistenceMapper.toEntity(emailVerification));
        return emailVerificationPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<EmailVerification> findLatestByEmailAndUsedAndType(
            EmailAddress email, Boolean used, EmailVerificationType type) {
        return emailVerificationJpaRepository
                .findFirstByEmailAndUsedAndEmailVerificationTypeOrderByCreatedAtDesc(email.value(), used, type)
                .map(emailVerificationPersistenceMapper::toDomain);
    }
}
