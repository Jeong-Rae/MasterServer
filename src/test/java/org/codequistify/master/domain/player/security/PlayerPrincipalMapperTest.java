package org.codequistify.master.domain.player.security;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccesses;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.EncodedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerPrincipalMapperTest {

    private final PlayerPrincipalMapper mapper = new PlayerPrincipalMapper();

    @Test
    void mapsDomainPlayerToSecurityPrincipalOnly() {
        Player player = Player.create(
                new PlayerId("018f2fcb-7c0a-7cc0-8a2a-111111111111"),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(3)),
                PlayerAuthentication.local(new EncodedPassword("encoded")),
                new PlayerAuthorization(
                        PlayerRoles.of(PlayerRoleType.PLAYER),
                        PlayerAccesses.of(PlayerAccessType.BASIC_PROBLEMS_ACCESS)));

        PlayerPrincipal principal = mapper.toPrincipal(player);

        assertEquals(player.id().value(), principal.id());
        assertEquals(player.profile().email().value(), principal.email());
        assertEquals(player.authentication().encodedPassword().value(), principal.getPassword());
        assertEquals(player.id().value(), principal.getUsername());
        assertTrue(principal.roles().contains(PlayerRoleType.PLAYER));
        assertTrue(principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(PlayerRoleType.PLAYER.getRole())));
    }
}
