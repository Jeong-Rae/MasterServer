package org.codequistify.master.domain.player.persistence;

import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.PlayerStatus;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.authorization.PlayerAccessType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerPersistenceMapperTest {

    private final PlayerPersistenceMapper mapper = new PlayerPersistenceMapper();

    @Test
    void mapsEntityToDomainAndBackWithoutLeakingJpaModel() {
        PlayerEntity entity = PlayerEntity.builder()
                .id("018f2fcb-7c0a-7cc0-8a2a-111111111111")
                .name("pol")
                .email("pol@example.com")
                .password("encoded")
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

        assertEquals(entity.getId(), player.id().value());
        assertEquals("pol", player.profile().name().value());
        assertTrue(player.authorization().hasRole(PlayerRoleType.PLAYER));
        assertTrue(player.authorization().hasAccess(PlayerAccessType.BASIC_PROBLEMS_ACCESS));
        assertEquals(PlayerStatus.ACTIVE, player.status());
        assertEquals(entity.getId(), mapped.getId());
        assertEquals(entity.getRoles(), mapped.getRoles());
        assertEquals(entity.getRefreshToken(), mapped.getRefreshToken());
    }
}
