package org.codequistify.master.domain.player.dto;

import java.util.List;
import org.codequistify.master.domain.player.domain.Player;

public record PlayerProfileResponse(String id, String name, String email, Integer level, List<String> roles) {
    public static PlayerProfileResponse from(Player player) {
        return new PlayerProfileResponse(
                player.id().value(),
                player.profile().name().value(),
                player.profile().email().value(),
                player.profile().experience().value(),
                player.authorization().toStoredAuthorities());
    }
}
