package org.codequistify.master.domain.player.domain.vo;

public record RawPassword(String value) {
    public RawPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("raw password must not be blank");
        }
    }
}
