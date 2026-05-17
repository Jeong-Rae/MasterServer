package org.codequistify.master.domain.lab.domain.vo;

import org.codequistify.master.global.uuid.UUIDv7Generator;

public record VirtualWorkspaceId(String value) {
    public VirtualWorkspaceId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("virtual workspace id must not be blank");
        }
    }

    public static VirtualWorkspaceId newId() {
        return new VirtualWorkspaceId(UUIDv7Generator.generate());
    }
}
