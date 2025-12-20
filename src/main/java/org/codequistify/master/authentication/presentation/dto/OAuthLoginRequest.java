package org.codequistify.master.authentication.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.codequistify.master.authentication.domain.model.OAuthProvider;

public record OAuthLoginRequest(
    @NotNull OAuthProvider provider,
    @NotBlank String authorizationCode,
    @NotBlank String nicknameCandidate) {}
