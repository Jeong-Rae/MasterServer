package org.codequistify.master.domain.player.domain.vo;

public record OAuthAccessToken(String value) {
    public OAuthAccessToken {
        if (value == null) {
            value = "";
        }
    }

    public boolean isBlank() {
        return value.isBlank();
    }
}
