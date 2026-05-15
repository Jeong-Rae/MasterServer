package org.codequistify.master.global.jwt.vo;

import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccessTokenClaimsTest {

    @Test
    void AccessToken_claims에서_얇은_Principal을_생성한다() {
        AccessTokenClaims claims = new AccessTokenClaims(
                TokenAudience.from("player-id"),
                TokenAuthorities.fromStoredAuthorities(List.of(
                        PlayerRoleType.PLAYER.getRole(),
                        PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission())));

        PlayerPrincipal principal = claims.toPrincipal();

        assertThat(principal.id().value()).isEqualTo("player-id");
        assertThat(principal.getPassword()).isEmpty();
        assertThat(principal.roles().contains(PlayerRoleType.PLAYER)).isTrue();
        assertThat(principal.accesses().contains(PlayerAccessType.BASIC_PROBLEMS_ACCESS)).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
    }
}
