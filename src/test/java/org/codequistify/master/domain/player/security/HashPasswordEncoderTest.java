package org.codequistify.master.domain.player.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.RawPassword;
import org.junit.jupiter.api.Test;

class HashPasswordEncoderTest {

    private final HashPasswordEncoder hashPasswordEncoder = new HashPasswordEncoder();

    @Test
    void 원문_비밀번호를_해시_비밀번호로_변환하면_bcrypt_접두사를_붙인다() {
        HashedPassword hashedPassword = hashPasswordEncoder.encode(RawPassword.middleLevel("password1!"));

        assertThat(hashedPassword.value()).startsWith("{bcrypt}");
    }

    @Test
    void 원문_비밀번호와_해시_비밀번호를_비교한다() {
        RawPassword rawPassword = RawPassword.middleLevel("password1!");
        HashedPassword hashedPassword = hashPasswordEncoder.encode(rawPassword);

        assertThat(hashPasswordEncoder.matches(rawPassword, hashedPassword)).isTrue();
        assertThat(hashPasswordEncoder.matches(new RawPassword("wrongpassword1!"), hashedPassword))
                .isFalse();
    }
}
