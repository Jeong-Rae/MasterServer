package org.codequistify.master.stage.domain.vo;

import org.codequistify.master.global.uuid.UUIDv7Generator;

public record StageId(String value) {
    public StageId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("stage id must not be blank");
        }
    }

    public static StageId newId() {
        return new StageId(UUIDv7Generator.generate());
    }

    public static StageId of(String value) {
        return new StageId(value);
    }
}
