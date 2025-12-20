package org.codequistify.master.authentication.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @NotBlank String email,
    @NotBlank String password,
    @NotBlank String nickname) {}
