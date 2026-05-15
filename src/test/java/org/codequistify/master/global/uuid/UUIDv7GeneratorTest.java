package org.codequistify.master.global.uuid;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UUIDv7GeneratorTest {

    @Test
    void 유유아이디를_생성하면_7버전_문자열을_반환한다() {
        String id = UUIDv7Generator.generate();

        assertThatCode(() -> UUID.fromString(id)).doesNotThrowAnyException();
        UUID uuid = UUID.fromString(id);
        assertThat(uuid.version()).isEqualTo(7);
    }
}
