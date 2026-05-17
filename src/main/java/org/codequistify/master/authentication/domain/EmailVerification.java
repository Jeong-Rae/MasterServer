package org.codequistify.master.domain.authentication.domain;

import org.codequistify.master.domain.authentication.vo.EmailVerificationId;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.shared.vo.EmailAddress;

public class EmailVerification {
    private final EmailVerificationId id;
    private final EmailAddress email;
    private final VerificationCode code;
    private boolean verified;
    private boolean used;
    private final EmailVerificationType type;

    public EmailVerification(
            EmailVerificationId id,
            EmailAddress email,
            VerificationCode code,
            boolean verified,
            boolean used,
            EmailVerificationType type) {
        if (id == null) {
            id = EmailVerificationId.empty();
        }
        if (email == null || code == null || type == null) {
            throw new IllegalArgumentException("email verification fields must not be null");
        }
        this.id = id;
        this.email = email;
        this.code = code;
        this.verified = verified;
        this.used = used;
        this.type = type;
    }

    public static EmailVerification issue(EmailAddress email, VerificationCode code, EmailVerificationType type) {
        return new EmailVerification(EmailVerificationId.empty(), email, code, false, false, type);
    }

    public EmailVerification verify() {
        this.verified = true;
        return this;
    }

    public EmailVerification markAsUsed() {
        this.used = true;
        return this;
    }

    public boolean matches(VerificationCode code) {
        return this.code.matches(code);
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isUsed() {
        return used;
    }

    public EmailVerificationId id() {
        return id;
    }

    public EmailAddress email() {
        return email;
    }

    public VerificationCode code() {
        return code;
    }

    public EmailVerificationType type() {
        return type;
    }
}
