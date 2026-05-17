package org.codequistify.master.domain.player.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccesses;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.HashedPassword;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.junit.jupiter.api.Test;

class PlayerPrincipalMapperTest {

    private final PlayerPrincipalMapper mapper = new PlayerPrincipalMapper();

    @Test
    void 플레이어_도메인을_인증_주체로_변환하면_보안_인증정보만_반환한다() {
        Player player = Player.create(
                new PlayerId("018f2fcb-7c0a-7cc0-8a2a-111111111111"),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(3)),
                PlayerAuthentication.local(new HashedPassword("{bcrypt}encoded")),
                new PlayerAuthorization(
                        PlayerRoles.of(PlayerRoleType.PLAYER),
                        PlayerAccesses.of(PlayerAccessType.BASIC_PROBLEMS_ACCESS)));

        PlayerPrincipal principal = mapper.toPrincipal(player);

        assertThat(principal.id()).isEqualTo(player.id());
        assertThat(principal.getPassword()).isEmpty();
        assertThat(principal.getUsername()).isEqualTo(player.id().value());
        assertThat(principal.roles().contains(PlayerRoleType.PLAYER)).isTrue();
        assertThat(principal.getAuthorities())
                .anyMatch(authority -> authority.getAuthority().equals(PlayerRoleType.PLAYER.getRole()));
    }
}
