package org.codequistify.master.domain.authentication.vo;

public record EmailVerificationId(Long value) {
    public static EmailVerificationId empty() {
        return new EmailVerificationId(null);
    }

    public boolean isEmpty() {
        return value == null;
    }
}
