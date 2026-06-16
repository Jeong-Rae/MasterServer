package org.codequistify.master.domain.player.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, UUID> {
    Optional<PlayerEntity> findByEmail(String email);

    Optional<PlayerEntity> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);

    boolean existsByNameIgnoreCase(String name);
}
