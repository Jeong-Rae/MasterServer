package org.codequistify.master.domain.authentication.dto;

import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.global.jwt.dto.TokenResponse;

public record LoginResponse(PlayerProfileResponse profile, TokenResponse token) {}
