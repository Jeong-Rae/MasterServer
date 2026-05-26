package org.codequistify.master.domain.player.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.dto.PlayerProfileResponse;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.persistence.PlayerRepository;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.PlayerAuthorizationGuard;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPointResponse;
import org.codequistify.master.stage.service.StageSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerProfileService {
    private final PlayerRepository playerRepository;
    private final StageSearchService stageSearchService;
    private final PlayerAuthorizationGuard playerAuthorizationGuard;

    @LogExecutionTime
    @Transactional(readOnly = true)
    public List<PlayerProfileResponse> findAllPlayerProfiles(AuthenticatedPlayer requester) {
        playerAuthorizationGuard.requireAdmin(requester);
        return playerRepository.findAllPlayers().stream()
                .map(PlayerProfileResponse::from)
                .collect(Collectors.toList());
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public PlayerStageProgressResponse getMyStagesByStatus(AuthenticatedPlayer requester, CompletedStatus status) {
        if (status.equals(CompletedStatus.NOT_COMPLETED)) {
            throw new BusinessException(ErrorCode.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
        if (status.equals(CompletedStatus.COMPLETED)) {
            return getCompletedStagesByPlayer(requester);
        }
        if (status.equals(CompletedStatus.IN_PROGRESS)) {
            return getInProgressStagesByPlayer(requester);
        }
        return new PlayerStageProgressResponse(List.of());
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public PlayerStageProgressResponse getCompletedStagesByPlayer(AuthenticatedPlayer requester) {
        return stageSearchService.getCompletedStagesByPlayer(requester);
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public PlayerStageProgressResponse getInProgressStagesByPlayer(AuthenticatedPlayer requester) {
        return stageSearchService.getInProgressStagesByPlayer(requester);
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public List<StageAttemptHeatMapPointResponse> getHeatMapDataPointsByModifiedDate(AuthenticatedPlayer requester) {
        return stageSearchService.getHeatMapDataPointsByModifiedDate(requester);
    }

    @Transactional
    public Integer increaseExp(Player player, int point) {
        Player updated = playerRepository.save(player.increaseExperience(point));
        return updated.profile().experience().value();
    }

    @Transactional(readOnly = true)
    public boolean isDuplicatedName(String name) {
        return playerRepository.existsProfileName(new PlayerName(name));
    }
}
