package org.codequistify.master.player.infrastructure.persistence.repository;

import java.util.UUID;
import org.codequistify.master.player.infrastructure.persistence.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {
  boolean existsByNicknameIgnoreCase(String nickname);
}
