package org.codequistify.master.domain.lab.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualWorkspaceEventJpaRepository extends JpaRepository<VirtualWorkspaceEventEntity, UUID> {
    Optional<VirtualWorkspaceEventEntity> findByIdempotencyKey(String idempotencyKey);

    List<VirtualWorkspaceEventEntity> findAllByWorkspaceIdOrderByOccurredAtAsc(UUID workspaceId);
}
