package org.codequistify.master.global.uuid;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UUIDv7GeneratorTest {

    @Test
    void generatesUuidVersion7String() {
        String id = UUIDv7Generator.generate();

        UUID uuid = assertDoesNotThrow(() -> UUID.fromString(id));
        assertEquals(7, uuid.version());
    }
}
