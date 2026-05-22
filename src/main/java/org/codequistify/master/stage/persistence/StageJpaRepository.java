package org.codequistify.master.stage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StageJpaRepository extends JpaRepository<StageEntity, String> {}
