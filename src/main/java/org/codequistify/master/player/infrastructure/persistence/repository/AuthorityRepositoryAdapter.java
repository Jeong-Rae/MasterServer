package org.codequistify.master.player.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.application.port.AuthorityRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.authority.Authority;
import org.codequistify.master.player.infrastructure.persistence.entity.AuthorityEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthorityRepositoryAdapter implements AuthorityRepository {
  private final AuthorityJpaRepository authorityJpaRepository;

  @Override
  public Optional<Authority> findByPlayerId(PlayerId playerId) {
    return authorityJpaRepository.findById(playerId.value()).map(AuthorityEntity::toDomain);
  }

  @Override
  public Authority save(Authority authority) {
    return authorityJpaRepository.save(AuthorityEntity.from(authority)).toDomain();
  }
}
