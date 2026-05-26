package org.codequistify.master.shared.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

class BaseTimeEntityTest {

    @Test
    void BaseTimeEntity는_created_at과_updated_at_컬럼을_사용한다() throws NoSuchFieldException {
        Column createdAt = BaseTimeEntity.class.getDeclaredField("createdAt").getAnnotation(Column.class);
        Column updatedAt = BaseTimeEntity.class.getDeclaredField("updatedAt").getAnnotation(Column.class);

        assertThat(createdAt.name()).isEqualTo("created_at");
        assertThat(updatedAt.name()).isEqualTo("updated_at");
    }
}
