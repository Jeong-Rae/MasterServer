package org.codequistify.master.domain.authentication.vo;

import org.codequistify.master.shared.vo.EmailAddress;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public record VerificationCode(String value) {
    public VerificationCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("verification code must not be blank");
        }
    }

    public static VerificationCode issue(EmailAddress email, MailSecret mailSecret) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update((email.value() + mailSecret.value()).getBytes(StandardCharsets.UTF_8));
            return new VerificationCode(String.format("%064x", new BigInteger(1, messageDigest.digest())));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm is not available", exception);
        }
    }

    public boolean matches(VerificationCode verificationCode) {
        return equals(verificationCode);
    }
}
