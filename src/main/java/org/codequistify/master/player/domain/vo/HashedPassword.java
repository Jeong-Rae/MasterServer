package org.codequistify.master.domain.player.domain.vo;

public record HashedPassword(String value) {
    public static final String BCRYPT_PREFIX = "{bcrypt}";

    public HashedPassword {
        if (value == null) {
            value = "";
        }
        if (!value.isBlank() && !value.startsWith(BCRYPT_PREFIX)) {
            throw new IllegalArgumentException("hashed password must start with {bcrypt}");
        }
    }

    public static HashedPassword bcrypt(String encodedBody) {
        if (encodedBody == null || encodedBody.isBlank()) {
            throw new IllegalArgumentException("bcrypt password body must not be blank");
        }
        return new HashedPassword(BCRYPT_PREFIX + encodedBody);
    }

    public String encodedBody() {
        if (value.isBlank()) {
            return "";
        }
        return value.substring(BCRYPT_PREFIX.length());
    }

    public boolean isBlank() {
        return value.isBlank();
    }
}
