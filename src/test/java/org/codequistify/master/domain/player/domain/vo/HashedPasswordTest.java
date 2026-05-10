package org.codequistify.master.domain.player.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashedPasswordTest {

    @Test
    void bcrypt_접두사가_있는_해시를_생성하면_접두사와_본문을_분리해_반환한다() {
        HashedPassword password = new HashedPassword("{bcrypt}$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFG");

        assertThat(password.value()).isEqualTo("{bcrypt}$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFG");
        assertThat(password.encodedBody()).isEqualTo("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFG");
    }

    @Test
    void bcrypt_접두사가_없는_해시를_생성하면_예외를_던진다() {
        assertThatThrownBy(() -> new HashedPassword("$2a$10$abcdefghijklmnopqrstuvwxyzABCDEFG"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 원문_비밀번호로_해시를_생성하면_예외를_던진다() {
        assertThatThrownBy(() -> new HashedPassword("password99763892*"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 외부_인증_전용_계정의_빈_비밀번호를_생성하면_빈_값을_반환한다() {
        assertThat(new HashedPassword(null).value()).isEmpty();
        assertThat(new HashedPassword("").value()).isEmpty();
    }
}
