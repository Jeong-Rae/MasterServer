package org.codequistify.master.domain.player.persistence;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.PlayerStatus;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerPersistenceMapperTest {

    private final PlayerPersistenceMapper mapper = new PlayerPersistenceMapper();

    @Test
    void 엔티티를_도메인으로_변환후_다시_엔티티로_변환하면_동일한_저장값을_반환한다() {
        PlayerEntity entity = PlayerEntity.builder()
                .id("018f2fcb-7c0a-7cc0-8a2a-111111111111")
                .name("pol")
                .email("pol@example.com")
                .password("{bcrypt}encoded")
                .oAuthType(OAuthType.POL)
                .oAuthId("0")
                .roles(List.of(PlayerRoleType.PLAYER.getRole(), PlayerAccessType.BASIC_PROBLEMS_ACCESS.getPermission()))
                .locked(false)
                .oAuthAccessToken("access")
                .refreshToken("refresh")
                .exp(10)
                .build();

        Player player = mapper.toDomain(entity);
        PlayerEntity mapped = mapper.toEntity(player);

        assertThat(player.id().value()).isEqualTo(entity.getId());
        assertThat(player.profile().name().value()).isEqualTo("pol");
        assertThat(player.authorization().hasRole(PlayerRoleType.PLAYER)).isTrue();
        assertThat(player.authorization().hasAccess(PlayerAccessType.BASIC_PROBLEMS_ACCESS)).isTrue();
        assertThat(player.status()).isEqualTo(PlayerStatus.ACTIVE);
        assertThat(mapped.getId()).isEqualTo(entity.getId());
        assertThat(mapped.getRoles()).isEqualTo(entity.getRoles());
        assertThat(mapped.getPassword()).isEqualTo("{bcrypt}encoded");
        assertThat(mapped.getRefreshToken()).isEqualTo(entity.getRefreshToken());
    }

    @Test
    void 접두사가_없는_저장_비밀번호를_도메인으로_변환하면_예외를_던진다() {
        PlayerEntity entity = PlayerEntity.builder()
                .id("018f2fcb-7c0a-7cc0-8a2a-111111111111")
                .name("pol")
                .email("pol@example.com")
                .password("encoded")
                .oAuthType(OAuthType.POL)
                .oAuthId("0")
                .roles(List.of(PlayerRoleType.PLAYER.getRole()))
                .locked(false)
                .build();

        assertThatThrownBy(() -> mapper.toDomain(entity))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
