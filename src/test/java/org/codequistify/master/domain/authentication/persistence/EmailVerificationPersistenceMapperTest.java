package org.codequistify.master.domain.authentication.persistence;

import org.codequistify.master.domain.authentication.domain.EmailVerification;
import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.vo.EmailVerificationId;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.shared.vo.EmailAddress;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailVerificationPersistenceMapperTest {

    private final EmailVerificationPersistenceMapper mapper = new EmailVerificationPersistenceMapper();

    @Test
    void Entity를_Domain으로_변환한다() {
        EmailVerificationEntity entity = EmailVerificationEntity.builder()
                .id(1L)
                .email("pol@example.com")
                .code("code-123")
                .verified(true)
                .used(false)
                .emailVerificationType(EmailVerificationType.REGISTRATION)
                .build();

        EmailVerification domain = mapper.toDomain(entity);

        assertThat(domain.id().value()).isEqualTo(1L);
        assertThat(domain.email().value()).isEqualTo("pol@example.com");
        assertThat(domain.code().value()).isEqualTo("code-123");
        assertThat(domain.isVerified()).isTrue();
        assertThat(domain.isUsed()).isFalse();
        assertThat(domain.type()).isEqualTo(EmailVerificationType.REGISTRATION);
    }

    @Test
    void Domain을_Entity로_변환한다() {
        EmailVerification domain = new EmailVerification(
                new EmailVerificationId(1L),
                new EmailAddress("pol@example.com"),
                new VerificationCode("code-123"),
                true,
                true,
                EmailVerificationType.PASSWORD_RESET);

        EmailVerificationEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getEmail()).isEqualTo("pol@example.com");
        assertThat(entity.getCode()).isEqualTo("code-123");
        assertThat(entity.getVerified()).isTrue();
        assertThat(entity.getUsed()).isTrue();
        assertThat(entity.getEmailVerificationType()).isEqualTo(EmailVerificationType.PASSWORD_RESET);
    }
}
