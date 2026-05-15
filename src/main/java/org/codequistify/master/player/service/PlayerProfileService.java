package org.codequistify.master.domain.player.service;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoleType;
import org.codequistify.master.domain.player.domain.authorization.PlayerRoles;
import org.codequistify.master.domain.player.dto.PlayerProfile;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.persistence.PlayerRepository;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.stage.dto.HeatMapDataPoint;
import org.codequistify.master.domain.stage.service.StageSearchService;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {
    private final PlayerRepository playerRepository;
    private final StageSearchService stageSearchService;

    @LogExecutionTime
    @Transactional
    public List<PlayerProfile> findAllPlayerProfiles() {
        return playerRepository.findAllPlayers().stream()
                .map(PlayerProfile::from)
                .collect(Collectors.toList());
    }

    @LogExecutionTime
    @Transactional
    public PlayerStageProgressResponse getCompletedStagesByPlayerId(PlayerPrincipal principal) {
        return stageSearchService.getCompletedStagesByPlayerId(principal.id().value());
    }

    @LogExecutionTime
    @Transactional
    public PlayerStageProgressResponse getInProgressStagesByPlayerId(PlayerPrincipal principal) {
        return stageSearchService.getInProgressStagesByPlayerId(principal.id().value());
    }

    @LogExecutionTime
    @Transactional
    public List<HeatMapDataPoint> getHeatMapDataPointsByModifiedDate(PlayerPrincipal principal) {
        return stageSearchService.getHeatMapDataPointsByModifiedDate(principal.id().value());
    }

    public boolean isAdmin(PlayerPrincipal principal) {
        return PlayerRolesChecker.checkAnyRole(
                principal.roles(),
                PlayerRoles.of(PlayerRoleType.ADMIN, PlayerRoleType.SUPER_ADMIN));
    }

    @Transactional
    public Integer increaseExp(Player player, int point) {
        Player updated = playerRepository.save(player.increaseExperience(point));
        return updated.profile().experience().value();
    }

    @Transactional
    public boolean isDuplicatedName(String name) {
        return playerRepository.existsProfileName(new PlayerName(name));
    }


}
