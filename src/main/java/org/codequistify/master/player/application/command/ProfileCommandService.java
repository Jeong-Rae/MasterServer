package org.codequistify.master.player.application.command;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.port.ProfileRepository;
import java.util.UUID;
import org.codequistify.master.player.domain.model.Profile;
import org.codequistify.master.player.domain.vo.Nickname;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCommandService {
  private final ProfileRepository profileRepository;

  @Transactional
  public Profile createProfile(UUID playerUuid, String nickname) {
    PlayerId playerId = PlayerId.of(playerUuid);
    Nickname nicknameVo = toNickname(nickname);
    if (profileRepository.existsByNickname(nicknameVo)) {
      throw new BusinessException(ErrorCode.DUPLICATE_NAME, HttpStatus.BAD_REQUEST);
    }
    return profileRepository.save(new Profile(playerId, nicknameVo));
  }

  @Transactional
  public Profile changeNickname(UUID playerUuid, String nickname) {
    PlayerId playerId = PlayerId.of(playerUuid);
    Nickname nicknameVo = toNickname(nickname);
    if (profileRepository.existsByNickname(nicknameVo)) {
      throw new BusinessException(ErrorCode.DUPLICATE_NAME, HttpStatus.BAD_REQUEST);
    }
    Profile profile =
        profileRepository
            .findByPlayerId(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    profile.changeNickname(nicknameVo);
    return profileRepository.save(profile);
  }

  private Nickname toNickname(String nickname) {
    try {
      return new Nickname(nickname);
    } catch (IllegalArgumentException exception) {
      throw new BusinessException(ErrorCode.INVALID_NICKNAME, HttpStatus.BAD_REQUEST, exception);
    }
  }
}
