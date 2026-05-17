package org.codequistify.master.domain.player.persistence;

import java.util.Optional;
import org.codequistify.master.domain.player.domain.authentication.OAuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, String> {
    Optional<PlayerEntity> findByEmail(String email);

    Optional<PlayerEntity> findByRefreshToken(String refreshToken);

    @Query("SELECT p.oAuthType FROM PlayerEntity p WHERE p.email = :email")
    Optional<OAuthType> findOAuthTypeByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNameIgnoreCase(String name);
}
