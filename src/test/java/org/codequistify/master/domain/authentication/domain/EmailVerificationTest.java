package org.codequistify.master.domain.authentication.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import org.codequistify.master.domain.authentication.vo.EmailVerificationId;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.shared.vo.EmailAddress;
import org.junit.jupiter.api.Test;

class EmailVerificationTest {

    @Test
    void EmailVerification_도메인에는_JPA_Entity_어노테이션이_없다() {
        assertThat(EmailVerification.class.getAnnotation(Entity.class)).isNull();
    }

    @Test
    void Email과_Code와_Type으로_인증정보를_발급한다() {
        EmailVerification verification = EmailVerification.issue(
                new EmailAddress("pol@example.com"),
                new VerificationCode("code-123"),
                EmailVerificationType.REGISTRATION);

        assertThat(verification.email().value()).isEqualTo("pol@example.com");
        assertThat(verification.code().value()).isEqualTo("code-123");
        assertThat(verification.type()).isEqualTo(EmailVerificationType.REGISTRATION);
        assertThat(verification.isVerified()).isFalse();
    }

    @Test
    void 저장된_Code와_요청_Code가_같은지_판별한다() {
        EmailVerification verification = EmailVerification.issue(
                new EmailAddress("pol@example.com"),
                new VerificationCode("code-123"),
                EmailVerificationType.REGISTRATION);

        assertThat(verification.matches(new VerificationCode("code-123"))).isTrue();
        assertThat(verification.matches(new VerificationCode("code-456"))).isFalse();
    }

    @Test
    void verify를_호출하면_현재_도메인_상태가_인증완료로_변환된다() {
        EmailVerification verification = EmailVerification.issue(
                new EmailAddress("pol@example.com"),
                new VerificationCode("code-123"),
                EmailVerificationType.REGISTRATION);

        EmailVerification verified = verification.verify();

        assertThat(verified.isVerified()).isTrue();
    }

    @Test
    void markAsUsed를_호출하면_현재_도메인_상태가_사용완료로_변환된다() {
        EmailVerification verification = new EmailVerification(
                new EmailVerificationId(1L),
                new EmailAddress("pol@example.com"),
                new VerificationCode("code-123"),
                true,
                false,
                EmailVerificationType.REGISTRATION);

        EmailVerification used = verification.markAsUsed();

        assertThat(used.isUsed()).isTrue();
    }
}
