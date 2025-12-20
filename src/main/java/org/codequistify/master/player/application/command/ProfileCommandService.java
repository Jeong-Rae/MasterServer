package org.codequistify.master.player.application.command;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.port.ProfileRepository;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.profile.Nickname;
import org.codequistify.master.player.domain.profile.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCommandService {
  private final ProfileRepository profileRepository;

  @Transactional
  public Profile createProfile(PlayerId playerId, Nickname nickname) {
    if (profileRepository.existsByNickname(nickname)) {
      throw new BusinessException(ErrorCode.DUPLICATE_NAME, HttpStatus.BAD_REQUEST);
    }
    return profileRepository.save(new Profile(playerId, nickname));
  }

  @Transactional
  public Profile changeNickname(PlayerId playerId, Nickname nickname) {
    if (profileRepository.existsByNickname(nickname)) {
      throw new BusinessException(ErrorCode.DUPLICATE_NAME, HttpStatus.BAD_REQUEST);
    }
    Profile profile =
        profileRepository
            .findByPlayerId(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    profile.changeNickname(nickname);
    return profileRepository.save(profile);
  }
}
