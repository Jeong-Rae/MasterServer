package org.codequistify.master.domain.lab.domain.vo;

import org.codequistify.master.global.uuid.UUIDv7Generator;

public record VirtualWorkspaceEventId(String value) {
    public VirtualWorkspaceEventId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("virtual workspace event id must not be blank");
        }
    }

    public static VirtualWorkspaceEventId newId() {
        return new VirtualWorkspaceEventId(UUIDv7Generator.generate());
    }
}
