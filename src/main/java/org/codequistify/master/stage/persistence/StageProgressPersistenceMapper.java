package org.codequistify.master.stage.persistence;

import java.util.UUID;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.PlayerStageSolving;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;

public final class StageProgressPersistenceMapper {
    private StageProgressPersistenceMapper() {}

    public static PlayerStageProgressEntity toEntity(PlayerStageSolving solving) {
        return PlayerStageProgressEntity.create(
                new PlayerStageProgressEntityId(
                        UUID.fromString(solving.playerId().value()),
                        UUID.fromString(solving.stageId().value())),
                solving.status(),
                solving.currentQuestionIndex().value());
    }

    public static PlayerStageSolving toDomain(PlayerStageProgressEntity entity) {
        PlayerStageSolving solving = PlayerStageSolving.start(
                new PlayerId(entity.id().playerId().toString()),
                StageId.of(entity.id().stageId().toString()));
        return switch (entity.status()) {
            case IN_PROGRESS -> solving.progressTo(QuestionIndex.of(entity.currentQuestionIndex()));
            case COMPLETED -> solving.completeAt(QuestionIndex.of(entity.currentQuestionIndex()));
            case ABANDONED -> solving.abandon();
            case FAILED -> solving;
        };
    }
}
