package org.codequistify.master.domain.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TempAccessTokenRequest(
        @NotBlank(message = "4101") @Email(message = "4102") String email) {}
