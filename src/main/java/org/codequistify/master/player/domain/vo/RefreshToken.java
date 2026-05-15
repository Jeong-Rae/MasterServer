package org.codequistify.master.domain.player.domain.vo;

public record RefreshToken(String value) {
    public RefreshToken {
        if (value == null) {
            value = "";
        }
    }

    public boolean isBlank() {
        return value.isBlank();
    }

    public boolean matches(RefreshToken refreshToken) {
        return equals(refreshToken);
    }
}
