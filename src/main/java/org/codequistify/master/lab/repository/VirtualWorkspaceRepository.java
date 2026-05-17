package org.codequistify.master.domain.lab.repository;

import java.util.Optional;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;

public interface VirtualWorkspaceRepository {
    VirtualWorkspace save(VirtualWorkspace virtualWorkspace);

    Optional<VirtualWorkspace> findRunningByPlayerIdAndStageId(PlayerId playerId, Long stageId);

    Optional<VirtualWorkspace> findLatestActiveByPlayerIdAndStageId(PlayerId playerId, Long stageId);

    Optional<VirtualWorkspace> findLatestActiveByPlayerIdAndStageImage(PlayerId playerId, StageImageType stageImage);
}
