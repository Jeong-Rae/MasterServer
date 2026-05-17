package org.codequistify.master;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MasterApplicationTests {

    @Test
    void 애플리케이션_컨텍스트를_로드하면_성공한다() {
        assertThat(true).isTrue();
    }
}
