package org.codequistify.master.domain.player.domain.vo;

import java.util.regex.Pattern;

public record PlayerName(String value) {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z가-힣0-9]+$");

    public PlayerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("player name must not be blank");
        }
        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("player name must contain only Korean, English, or number");
        }
    }
}
