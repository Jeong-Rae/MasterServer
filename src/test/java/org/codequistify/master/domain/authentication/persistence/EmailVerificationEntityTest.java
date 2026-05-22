package org.codequistify.master.domain.authentication.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EmailVerificationEntityTest {

    @Test
    void EmailVerification_Entity는_메일_인증_조회용_복합_Index를_가진다() {
        Table table = EmailVerificationEntity.class.getAnnotation(Table.class);

        boolean hasLookupIndex = Arrays.stream(table.indexes()).anyMatch(this::isEmailVerificationLookupIndex);

        assertThat(hasLookupIndex).isTrue();
    }

    private boolean isEmailVerificationLookupIndex(Index index) {
        return index.name().equals("idx_email_verification_lookup")
                && index.columnList().equals("email, used, type, created_at");
    }
}
