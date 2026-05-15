package org.codequistify.master.domain.player.domain.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RawPasswordTest {

    @Test
    void 중간_강도_비밀번호가_소문자_숫자_특수문자_8자_이상이면_값을_보관한다() {
        RawPassword password = RawPassword.middleLevel("password1!");

        assertThat(password.value()).isEqualTo("password1!");
    }

    @Test
    void 중간_강도_비밀번호에_특수문자가_없으면_예외를_던진다() {
        assertThatThrownBy(() -> RawPassword.middleLevel("password1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 중간_강도_비밀번호에_숫자가_없으면_예외를_던진다() {
        assertThatThrownBy(() -> RawPassword.middleLevel("password!"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 중간_강도_비밀번호가_8자_미만이면_예외를_던진다() {
        assertThatThrownBy(() -> RawPassword.middleLevel("pass1!"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
