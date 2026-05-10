package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerPrincipalMapper {
    public PlayerPrincipal toPrincipal(Player player) {
        return new PlayerPrincipal(
                player.id().value(),
                player.profile().email().value(),
                player.authentication().encodedPassword().value(),
                player.authorization().roles(),
                player.authorization().accesses(),
                player.status().locked());
    }
}
