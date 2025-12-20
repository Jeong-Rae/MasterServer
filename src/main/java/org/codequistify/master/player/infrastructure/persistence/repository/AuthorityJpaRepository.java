package org.codequistify.master.player.infrastructure.persistence.repository;

import java.util.UUID;
import org.codequistify.master.player.infrastructure.persistence.entity.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityJpaRepository extends JpaRepository<AuthorityEntity, UUID> {}
