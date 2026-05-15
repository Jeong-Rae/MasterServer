package org.codequistify.master.global.jwt.vo;

import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenAudienceTest {

    @Test
    void 문자열_audience를_PlayerId로_복원한다() {
        TokenAudience audience = TokenAudience.from("player-id");

        assertThat(audience.id()).isEqualTo(new PlayerId("player-id"));
        assertThat(audience.value()).isEqualTo("player-id");
    }

    @Test
    void blank_audience는_예외를_던진다() {
        assertThatThrownBy(() -> TokenAudience.from(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
