package org.codequistify.master.stage.repository;

import java.util.Optional;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.PlayerStageSolving;
import org.codequistify.master.stage.domain.vo.StageId;

public interface StageProgressRepository {
    PlayerStageSolving save(PlayerStageSolving solving);

    Optional<PlayerStageSolving> findByPlayerIdAndStageId(PlayerId playerId, StageId stageId);

    boolean existsByPlayerIdAndStageId(PlayerId playerId, StageId stageId);
}
