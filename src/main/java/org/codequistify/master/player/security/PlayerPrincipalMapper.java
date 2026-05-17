package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.Player;
import org.springframework.stereotype.Component;

@Component
public class PlayerPrincipalMapper {
    public PlayerPrincipal toPrincipal(Player player) {
        return new PlayerPrincipal(
                player.id(), player.authorization(), player.status().locked());
    }
}
