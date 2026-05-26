package org.codequistify.master.domain.player.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.junit.jupiter.api.Test;

class AuthenticatedPlayerTest {

    @Test
    void Principal에서_서비스용_인증_플레이어를_생성한다() {
        PlayerPrincipal principal =
                new PlayerPrincipal(new PlayerId("player-id"), PlayerAuthorization.basicPlayer(), false);

        AuthenticatedPlayer authenticatedPlayer = AuthenticatedPlayer.from(principal);

        assertThat(authenticatedPlayer.id()).isEqualTo(principal.id());
        assertThat(authenticatedPlayer.authorization()).isEqualTo(principal.authorization());
    }
}
