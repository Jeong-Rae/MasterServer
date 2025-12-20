package org.codequistify.master.authentication.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.codequistify.master.authentication.domain.model.EmailVerificationType;

public record EmailVerificationRequest(
    @NotBlank String email, @NotNull EmailVerificationType type) {}
