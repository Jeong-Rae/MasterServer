package org.codequistify.master.domain.authentication.vo;

import org.codequistify.master.shared.vo.EmailAddress;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationCodeTest {

    @Test
    void Email과_MailSecret이_같으면_같은_인증_Code를_발급한다() {
        VerificationCode code = VerificationCode.issue(
                new EmailAddress("pol@example.com"),
                new MailSecret("mail-secret"));

        VerificationCode sameCode = VerificationCode.issue(
                new EmailAddress("pol@example.com"),
                new MailSecret("mail-secret"));

        assertThat(code.matches(sameCode)).isTrue();
    }

    @Test
    void Email이나_MailSecret이_다르면_다른_인증_Code를_발급한다() {
        VerificationCode code = VerificationCode.issue(
                new EmailAddress("pol@example.com"),
                new MailSecret("mail-secret"));

        VerificationCode otherCode = VerificationCode.issue(
                new EmailAddress("other@example.com"),
                new MailSecret("mail-secret"));

        assertThat(code.matches(otherCode)).isFalse();
    }

    @Test
    void blank_Code는_생성할_수_없다() {
        assertThatThrownBy(() -> new VerificationCode(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
