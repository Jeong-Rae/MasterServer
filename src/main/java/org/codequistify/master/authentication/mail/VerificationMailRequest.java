package org.codequistify.master.domain.authentication.mail;

import org.codequistify.master.domain.authentication.domain.EmailVerificationType;
import org.codequistify.master.domain.authentication.vo.VerificationCode;
import org.codequistify.master.shared.vo.EmailAddress;

public record VerificationMailRequest(
        EmailAddress to,
        VerificationCode code,
        EmailVerificationType type
) {
    public VerificationMailRequest {
        if (to == null || code == null || type == null) {
            throw new IllegalArgumentException("verification mail request fields must not be null");
        }
    }
}
