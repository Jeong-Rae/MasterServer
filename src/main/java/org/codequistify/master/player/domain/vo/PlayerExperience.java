package org.codequistify.master.domain.player.domain.vo;

public record PlayerExperience(int value) {
    public PlayerExperience {
        if (value < 0) {
            throw new IllegalArgumentException("player experience must not be negative");
        }
    }

    public PlayerExperience increase(int point) {
        return new PlayerExperience(value + point);
    }
}
