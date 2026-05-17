package org.codequistify.master.global.jwt.vo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.junit.jupiter.api.Test;

class TokenAuthoritiesTest {

    @Test
    void 저장된_authority_문자열을_인가_도메인으로_복원한다() {
        TokenAuthorities authorities = TokenAuthorities.fromStoredAuthorities(
                List.of(PlayerRoleType.PLAYER.getRole(), PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission()));

        assertThat(authorities.authorization().hasRole(PlayerRoleType.PLAYER)).isTrue();
        assertThat(authorities.authorization().hasAccess(PlayerAccessType.BASIC_PROBLEMS_ACCESS))
                .isTrue();
        assertThat(authorities.storedAuthorities())
                .containsExactly(
                        PlayerRoleType.PLAYER.getRole(), PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission());
    }
}
