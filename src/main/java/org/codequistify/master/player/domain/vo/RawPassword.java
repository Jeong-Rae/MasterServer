package org.codequistify.master.domain.player.domain.vo;

import java.util.regex.Pattern;

public record RawPassword(String value) {
    private static final Pattern MIDDLE_LEVEL_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[a-z\\d@$!%*#?&]{8,}$");

    public RawPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("raw password must not be blank");
        }
    }

    public static RawPassword middleLevel(String value) {
        RawPassword rawPassword = new RawPassword(value);
        if (!MIDDLE_LEVEL_PATTERN.matcher(rawPassword.value()).matches()) {
            throw new IllegalArgumentException("raw password must be middle level");
        }
        return rawPassword;
    }
}
