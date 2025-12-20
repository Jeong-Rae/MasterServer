package org.codequistify.master.player.infrastructure.persistence.repository;

import java.util.UUID;
import org.codequistify.master.player.infrastructure.persistence.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingJpaRepository extends JpaRepository<RatingEntity, UUID> {}
