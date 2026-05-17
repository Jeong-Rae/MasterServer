package org.codequistify.master.domain.lab.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VirtualWorkspaceEventJpaRepository extends JpaRepository<VirtualWorkspaceEventEntity, String> {
    Optional<VirtualWorkspaceEventEntity> findByIdempotencyKey(String idempotencyKey);

    List<VirtualWorkspaceEventEntity> findAllByWorkspaceIdOrderByOccurredAtAsc(String workspaceId);
}
