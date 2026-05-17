package org.codequistify.master.domain.player.dto;

import java.util.List;
import org.codequistify.master.domain.player.domain.Player;

public record PlayerProfile(String id, String name, String email, Integer level, List<String> roles) {
    public static PlayerProfile from(Player player) {
        return new PlayerProfile(
                player.id().value(),
                player.profile().name().value(),
                player.profile().email().value(),
                player.profile().experience().value(),
                player.authorization().toStoredAuthorities());
    }
}
