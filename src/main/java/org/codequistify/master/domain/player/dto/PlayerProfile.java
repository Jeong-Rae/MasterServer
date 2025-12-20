package org.codequistify.master.domain.player.dto;

import java.util.List;
import java.util.UUID;

public record PlayerProfile(
    String uid, String name, String email, Integer level, List<String> roles, UUID playerId) {}
