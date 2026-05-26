package org.codequistify.master.domain.lab.persistence;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStateChangeEvent;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.repository.VirtualWorkspaceEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VirtualWorkspaceEventRepositoryAdapter implements VirtualWorkspaceEventRepository {
    private final VirtualWorkspaceEventJpaRepository virtualWorkspaceEventJpaRepository;
    private final VirtualWorkspaceEventPersistenceMapper virtualWorkspaceEventPersistenceMapper;

    @Override
    public VirtualWorkspaceStateChangeEvent saveIfAbsent(VirtualWorkspaceStateChangeEvent event) {
        return virtualWorkspaceEventJpaRepository
                .findByIdempotencyKey(event.idempotencyKey())
                .map(virtualWorkspaceEventPersistenceMapper::toDomain)
                .orElseGet(() -> save(event));
    }

    @Override
    public List<VirtualWorkspaceStateChangeEvent> findAllByWorkspaceId(VirtualWorkspaceId workspaceId) {
        return virtualWorkspaceEventJpaRepository
                .findAllByWorkspaceIdOrderByOccurredAtAsc(UUID.fromString(workspaceId.value()))
                .stream()
                .map(virtualWorkspaceEventPersistenceMapper::toDomain)
                .toList();
    }

    private VirtualWorkspaceStateChangeEvent save(VirtualWorkspaceStateChangeEvent event) {
        try {
            VirtualWorkspaceEventEntity saved =
                    virtualWorkspaceEventJpaRepository.save(virtualWorkspaceEventPersistenceMapper.toEntity(event));
            return virtualWorkspaceEventPersistenceMapper.toDomain(saved);
        } catch (DataIntegrityViolationException exception) {
            return virtualWorkspaceEventJpaRepository
                    .findByIdempotencyKey(event.idempotencyKey())
                    .map(virtualWorkspaceEventPersistenceMapper::toDomain)
                    .orElseThrow(() -> exception);
        }
    }
}
