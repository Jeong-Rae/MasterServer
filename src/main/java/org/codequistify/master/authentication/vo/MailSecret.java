package org.codequistify.master.domain.authentication.vo;

public record MailSecret(String value) {
    public MailSecret {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("mail secret must not be blank");
        }
    }
}
