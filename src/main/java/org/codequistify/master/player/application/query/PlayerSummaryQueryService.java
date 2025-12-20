package org.codequistify.master.player.application.query;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.player.application.query.view.PlayerSummaryView;
import org.codequistify.master.player.domain.PlayerId;
import org.codequistify.master.player.domain.authority.Authority;
import org.codequistify.master.player.domain.profile.Profile;
import org.codequistify.master.player.domain.rating.Rating;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerSummaryQueryService {
  private final ProfileQueryService profileQueryService;
  private final RatingQueryService ratingQueryService;
  private final AuthorityQueryService authorityQueryService;

  public PlayerSummaryView getSummary(PlayerId playerId) {
    // NOTE: activity, ban status, and ranking aggregation are intentionally excluded.
    Profile profile =
        profileQueryService
            .findProfile(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    Rating rating =
        ratingQueryService
            .findRating(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));
    Authority authority =
        authorityQueryService
            .findAuthority(playerId)
            .orElseThrow(
                () -> new BusinessException(ErrorCode.PLAYER_NOT_FOUND, HttpStatus.NOT_FOUND));

    return new PlayerSummaryView(
        playerId.value(),
        profile.nickname().value(),
        rating.point().value(),
        authority.roleAuthorities(),
        authority.permissionAuthorities());
  }
}
