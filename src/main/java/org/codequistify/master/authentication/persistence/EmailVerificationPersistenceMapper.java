package org.codequistify.master.domain.authentication.persistence;

import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.vo.EmailVerificationId;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.shared.vo.EmailAddress;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationPersistenceMapper {
    public EmailVerification toDomain(EmailVerificationEntity entity) {
        return new EmailVerification(
                new EmailVerificationId(entity.getId()),
                new EmailAddress(entity.getEmail()),
                new VerificationCode(entity.getCode()),
                Boolean.TRUE.equals(entity.getVerified()),
                Boolean.TRUE.equals(entity.getUsed()),
                entity.getEmailVerificationType());
    }

    public EmailVerificationEntity toEntity(EmailVerification emailVerification) {
        return EmailVerificationEntity.builder()
                .id(emailVerification.id().value())
                .email(emailVerification.email().value())
                .code(emailVerification.code().value())
                .verified(emailVerification.isVerified())
                .used(emailVerification.isUsed())
                .emailVerificationType(emailVerification.type())
                .build();
    }
}
