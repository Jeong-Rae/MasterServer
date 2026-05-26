package org.codequistify.master.global.jwt.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank(message = "4101") String refreshToken) {}
