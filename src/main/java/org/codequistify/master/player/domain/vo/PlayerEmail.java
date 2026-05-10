package org.codequistify.master.domain.player.domain.vo;

public record PlayerEmail(String value) {
    public PlayerEmail {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("player email must not be blank");
        }
    }
}
