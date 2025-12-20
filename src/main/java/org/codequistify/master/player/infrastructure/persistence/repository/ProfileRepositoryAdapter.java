package org.codequistify.master.player.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.application.port.ProfileRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.profile.Nickname;
import org.codequistify.master.player.domain.profile.Profile;
import org.codequistify.master.player.infrastructure.persistence.entity.ProfileEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {
  private final ProfileJpaRepository profileJpaRepository;

  @Override
  public Optional<Profile> findByPlayerId(PlayerId playerId) {
    return profileJpaRepository.findById(playerId.value()).map(ProfileEntity::toDomain);
  }

  @Override
  public Profile save(Profile profile) {
    return profileJpaRepository.save(ProfileEntity.from(profile)).toDomain();
  }

  @Override
  public boolean existsByNickname(Nickname nickname) {
    return profileJpaRepository.existsByNicknameIgnoreCase(nickname.value());
  }
}
