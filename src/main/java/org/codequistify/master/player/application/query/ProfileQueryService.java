package org.codequistify.master.player.application.query;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.player.application.port.ProfileRepository;
import org.codequistify.master.player.domain.model.Profile;
import org.codequistify.master.player.domain.vo.Nickname;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileQueryService {
  private final ProfileRepository profileRepository;

  public Optional<Profile> findProfile(PlayerId playerId) {
    return profileRepository.findByPlayerId(playerId);
  }

  public boolean isDuplicatedNickname(Nickname nickname) {
    return profileRepository.existsByNickname(nickname);
  }
}
