package org.codequistify.master.stage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, QuestionEntityId> {}
