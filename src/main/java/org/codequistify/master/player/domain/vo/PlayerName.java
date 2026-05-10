package org.codequistify.master.domain.player.domain.vo;

public record PlayerName(String value) {
    public PlayerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("player name must not be blank");
        }
    }
}
