package org.codequistify.master.domain.authentication.vo;

public record OAuthCode(String value) {
    public OAuthCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("oauth code must not be blank");
        }
    }
}
