package org.codequistify.master.stage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StageProgressJpaRepository
        extends JpaRepository<PlayerStageProgressEntity, PlayerStageProgressEntityId> {}
