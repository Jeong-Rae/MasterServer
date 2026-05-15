package org.codequistify.master.global.jwt.vo;

public record AccessToken(String value) {
    public AccessToken {
        if (value == null) {
            value = "";
        }
    }

    public boolean isBlank() {
        return value.isBlank();
    }
}
