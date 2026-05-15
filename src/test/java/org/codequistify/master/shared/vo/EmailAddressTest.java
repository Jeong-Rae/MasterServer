package org.codequistify.master.shared.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailAddressTest {

    @Test
    void Email_형식으로_생성하면_값을_보관한다() {
        EmailAddress email = new EmailAddress("pol@example.com");

        assertThat(email.value()).isEqualTo("pol@example.com");
    }

    @Test
    void Email_형식이_아니면_예외를_던진다() {
        assertThatThrownBy(() -> new EmailAddress("not-email"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
