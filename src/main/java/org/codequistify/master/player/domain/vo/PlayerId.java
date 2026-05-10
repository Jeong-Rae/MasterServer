package org.codequistify.master.domain.player.domain.vo;

import org.codequistify.master.global.uuid.UUIDv7Generator;

public record PlayerId(String value) {
    public PlayerId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("player id must not be blank");
        }
    }

    public static PlayerId newId() {
        return new PlayerId(UUIDv7Generator.generate());
    }
}
