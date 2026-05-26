package org.codequistify.master.stage.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StageJpaRepository extends JpaRepository<StageEntity, UUID> {}
