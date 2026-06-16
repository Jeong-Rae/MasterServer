package org.codequistify.master.domain.player.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerRepositoryAdapter implements PlayerRepository {
    private final PlayerJpaRepository playerJpaRepository;
    private final PlayerPersistenceMapper playerPersistenceMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public Player save(Player player) {
        PlayerEntity saved = playerJpaRepository.save(playerPersistenceMapper.toEntity(player));
        return playerPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Player> findAllPlayers() {
        return playerJpaRepository.findAll().stream()
                .map(playerPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Player> findPlayerById(PlayerId id) {
        return playerJpaRepository.findById(UUID.fromString(id.value())).map(playerPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Player> findPlayerByEmail(PlayerEmail email) {
        return playerJpaRepository.findByEmail(email.value()).map(playerPersistenceMapper::toDomain);
    }

    @Override
    public Optional<OAuthType> findAuthenticationTypeByEmail(PlayerEmail email) {
        QPlayerEntity player = QPlayerEntity.playerEntity;
        return Optional.ofNullable(queryFactory
                .select(player.oAuthType)
                .from(player)
                .where(player.email.eq(email.value()))
                .fetchOne());
    }

    @Override
    public boolean existsPlayerByEmail(PlayerEmail email) {
        return playerJpaRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsProfileName(PlayerName name) {
        return playerJpaRepository.existsByNameIgnoreCase(name.value());
    }

    @Override
    public void saveRefreshToken(PlayerId id, RefreshToken refreshToken) {
        playerJpaRepository
                .findById(UUID.fromString(id.value()))
                .map(playerPersistenceMapper::toDomain)
                .map(player -> player.updateRefreshToken(refreshToken))
                .ifPresent(this::save);
    }
}
