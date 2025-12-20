package org.codequistify.master.domain.player.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.converter.PlayerConverter;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.PlayerRoleType;
import org.codequistify.master.domain.player.dto.PlayerProfile;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.repository.PlayerRepository;
import org.codequistify.master.domain.stage.dto.HeatMapDataPoint;
import org.codequistify.master.domain.stage.service.StageSearchService;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.player.application.command.RatingCommandService;
import org.codequistify.master.player.application.query.AuthorityQueryService;
import org.codequistify.master.player.application.query.ProfileQueryService;
import org.codequistify.master.player.domain.vo.Nickname;
import org.codequistify.master.player.domain.vo.PlayerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {
  private final PlayerRepository playerRepository;
  private final PlayerConverter playerConverter;
  private final StageSearchService stageSearchService;
  private final ProfileQueryService profileQueryService;
  private final AuthorityQueryService authorityQueryService;
  private final RatingCommandService ratingCommandService;

  @LogExecutionTime
  @Transactional
  public List<PlayerProfile> findAllPlayerProfiles() {
    return playerRepository.findAll().stream()
        .map(playerConverter::convert)
        .collect(Collectors.toList());
  }

  @LogExecutionTime
  @Transactional
  public PlayerStageProgressResponse getCompletedStagesByPlayerId(Player player) {
    // NOTE: Activity/heatmap projections are intentionally excluded from Player BC refactor.
    return stageSearchService.getCompletedStagesByPlayerId(player.id());
  }

  @LogExecutionTime
  @Transactional
  public PlayerStageProgressResponse getInProgressStagesByPlayerId(Player player) {
    // NOTE: Activity/heatmap projections are intentionally excluded from Player BC refactor.
    return stageSearchService.getInProgressStagesByPlayerId(player.id());
  }

  @LogExecutionTime
  @Transactional
  public List<HeatMapDataPoint> getHeatMapDataPointsByModifiedDate(Player player) {
    // NOTE: Activity/heatmap projections are intentionally excluded from Player BC refactor.
    return stageSearchService.getHeatMapDataPointsByModifiedDate(player.id());
  }

  public boolean isAdmin(Player player) {
    if (player.getPlayerUuid() == null) {
      return PlayerRolesChecker.checkAnyRole(
          player, List.of(PlayerRoleType.ADMIN.getRole(), PlayerRoleType.SUPER_ADMIN.getRole()));
    }
    return authorityQueryService
        .findAuthority(PlayerId.of(player.getPlayerUuid()))
        .map(authority -> authority.isAdmin())
        .orElseGet(() -> PlayerRolesChecker.checkAnyRole(
            player, List.of(PlayerRoleType.ADMIN.getRole(), PlayerRoleType.SUPER_ADMIN.getRole())));
  }

  @Transactional
  public Integer increaseExp(Player player, int point) {
    if (player.getPlayerUuid() != null) {
      ratingCommandService.addPoints(player.getPlayerUuid(), point);
    }
    int exp = player.increaseLevelPoint(point);
    playerRepository.save(player);
    return exp;
  }

  @Transactional
  public boolean isDuplicatedName(String name) {
    return profileQueryService.isDuplicatedNickname(new Nickname(name));
  }
}
