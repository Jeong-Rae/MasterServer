package org.codequistify.master.domain.player.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.global.config.QuerydslConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({PlayerRepositoryAdapter.class, PlayerPersistenceMapper.class, QuerydslConfig.class})
class PlayerRepositoryAdapterTest {
    @Autowired
    private PlayerJpaRepository playerJpaRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    void email로_Player의_OAuthType을_조회한다() {
        playerJpaRepository.save(PlayerEntity.builder()
                .name("pol")
                .email("pol@example.com")
                .password("{bcrypt}encoded")
                .oAuthType(OAuthType.GOOGLE)
                .oAuthId("google-id")
                .build());

        assertThat(playerRepository.findAuthenticationTypeByEmail(new PlayerEmail("pol@example.com")))
                .contains(OAuthType.GOOGLE);
    }
}
