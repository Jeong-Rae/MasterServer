package org.codequistify.master.stage.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.SolvingStatus;
import org.codequistify.master.stage.domain.StageGroupType;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPointResponse;
import org.codequistify.master.stage.dto.StageCodeResponse;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.dto.StageSearchRequest;
import org.codequistify.master.stage.repository.StageQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StageQueryRepositoryAdapter implements StageQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StageResponse> findStagesByCriteria(
            StageSearchRequest searchRequest, PlayerId playerId, Pageable pageable) {
        QStageEntity stage = QStageEntity.stageEntity;
        QPlayerStageProgressEntity progress = QPlayerStageProgressEntity.playerStageProgressEntity;
        UUID playerUuid = UUID.fromString(playerId.value());
        BooleanBuilder where = stageWhere(searchRequest, stage, progress, playerId);

        List<StageResponseRow> rows = queryFactory
                .select(Projections.constructor(
                        StageResponseRow.class,
                        stage.id,
                        stage.stageImage,
                        stage.title,
                        stage.description,
                        stage.stageGroup,
                        stage.difficultyLevel,
                        stage.questionCount,
                        completedStatusExpression(progress)))
                .from(stage)
                .leftJoin(progress)
                .on(progress.id.stageId.eq(stage.id).and(progress.id.playerId.eq(playerUuid)))
                .where(where)
                .orderBy(stage.createdAt.desc(), stage.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(stage.count())
                .from(stage)
                .leftJoin(progress)
                .on(progress.id.stageId.eq(stage.id).and(progress.id.playerId.eq(playerUuid)))
                .where(where)
                .fetchOne();

        List<StageResponse> responses =
                rows.stream().map(StageResponseRow::toResponse).toList();

        return new PageImpl<>(responses, pageable, total == null ? 0 : total);
    }

    @Override
    public List<StageCodeResponse> findStageCodesByProgressStatus(PlayerId playerId, CompletedStatus status) {
        QStageEntity stage = QStageEntity.stageEntity;
        QPlayerStageProgressEntity progress = QPlayerStageProgressEntity.playerStageProgressEntity;
        UUID playerUuid = UUID.fromString(playerId.value());

        return queryFactory
                .select(Projections.constructor(StageCodeResponse.class, stage.stageImage))
                .from(progress)
                .join(stage)
                .on(stage.id.eq(progress.id.stageId))
                .where(progress.id.playerId.eq(playerUuid).and(progress.status.eq(solvingStatusOf(status))))
                .fetch();
    }

    @Override
    public List<StageAttemptHeatMapPointResponse> countAttemptByDate(PlayerId playerId) {
        QQuestionAttemptEntity attempt = QQuestionAttemptEntity.questionAttemptEntity;
        UUID playerUuid = UUID.fromString(playerId.value());
        DateExpression<LocalDate> attemptedDate =
                Expressions.dateTemplate(LocalDate.class, "date({0})", attempt.attemptedAt);

        return queryFactory
                .select(Projections.constructor(StageAttemptHeatMapPointResponse.class, attemptedDate, attempt.count()))
                .from(attempt)
                .where(attempt.playerId.eq(playerUuid))
                .groupBy(attemptedDate)
                .fetch();
    }

    @Override
    public Optional<StageResponse> findStageSummaryById(String stageId) {
        QStageEntity stage = QStageEntity.stageEntity;
        return Optional.ofNullable(queryFactory
                        .select(stageResponseProjection(stage, CompletedStatus.NOT_COMPLETED))
                        .from(stage)
                        .where(stage.id.eq(UUID.fromString(stageId)).and(stage.status.eq(StageStatus.APPROVED)))
                        .fetchOne())
                .map(StageResponseRow::toResponse);
    }

    @Override
    public List<StageResponse> findApprovedStageSummaries() {
        QStageEntity stage = QStageEntity.stageEntity;
        return queryFactory
                .select(stageResponseProjection(stage, CompletedStatus.NOT_COMPLETED))
                .from(stage)
                .where(stage.status.eq(StageStatus.APPROVED))
                .fetch()
                .stream()
                .map(StageResponseRow::toResponse)
                .toList();
    }

    @Override
    public Optional<StageResponse> findPreviewBySearchText(String query) {
        QStageEntity stage = QStageEntity.stageEntity;
        return Optional.ofNullable(queryFactory
                        .select(stageResponseProjection(stage, CompletedStatus.NOT_COMPLETED))
                        .from(stage)
                        .where(stage.status
                                .eq(StageStatus.APPROVED)
                                .and(stage.title
                                        .containsIgnoreCase(query)
                                        .or(stage.description.containsIgnoreCase(query))
                                        .or(stage.stageImage.stringValue().containsIgnoreCase(query))))
                        .limit(1)
                        .fetchOne())
                .map(StageResponseRow::toResponse);
    }

    private ConstructorExpression<StageResponseRow> stageResponseProjection(
            QStageEntity stage, CompletedStatus completedStatus) {
        return Projections.constructor(
                StageResponseRow.class,
                stage.id,
                stage.stageImage,
                stage.title,
                stage.description,
                stage.stageGroup,
                stage.difficultyLevel,
                stage.questionCount,
                Expressions.constant(completedStatus));
    }

    private BooleanBuilder stageWhere(
            StageSearchRequest searchRequest,
            QStageEntity stage,
            QPlayerStageProgressEntity progress,
            PlayerId playerId) {
        BooleanBuilder where = new BooleanBuilder();
        where.and(stage.status.eq(StageStatus.APPROVED));

        if (searchRequest.getStageGroupTypes() != null
                && !searchRequest.getStageGroupTypes().isEmpty()) {
            where.and(stage.stageGroup.in(searchRequest.getStageGroupTypes()));
        }
        if (searchRequest.getDifficultyLevels() != null
                && !searchRequest.getDifficultyLevels().isEmpty()) {
            where.and(stage.difficultyLevel.in(searchRequest.getDifficultyLevels()));
        }
        if (searchRequest.getSearchText() != null
                && !searchRequest.getSearchText().isBlank()) {
            String searchText = searchRequest.getSearchText();
            where.and(stage.title
                    .containsIgnoreCase(searchText)
                    .or(stage.description.containsIgnoreCase(searchText))
                    .or(stage.stageImage.stringValue().containsIgnoreCase(searchText)));
        }
        if (searchRequest.getCompletedStatus() == null) {
            return where;
        }
        if (searchRequest.getCompletedStatus() == CompletedStatus.NOT_COMPLETED) {
            return where.and(progress.id.playerId.isNull());
        }
        return where.and(progress.id.playerId.eq(UUID.fromString(playerId.value())))
                .and(progress.status.eq(solvingStatusOf(searchRequest.getCompletedStatus())));
    }

    private SolvingStatus solvingStatusOf(CompletedStatus status) {
        if (status == CompletedStatus.COMPLETED) {
            return SolvingStatus.COMPLETED;
        }
        return SolvingStatus.IN_PROGRESS;
    }

    private SimpleExpression<CompletedStatus> completedStatusExpression(QPlayerStageProgressEntity progress) {
        return Expressions.cases()
                .when(progress.status.eq(SolvingStatus.COMPLETED))
                .then(CompletedStatus.COMPLETED)
                .when(progress.status.eq(SolvingStatus.IN_PROGRESS))
                .then(CompletedStatus.IN_PROGRESS)
                .otherwise(CompletedStatus.NOT_COMPLETED);
    }

    private record StageResponseRow(
            UUID id,
            StageImageType stageImage,
            String title,
            String description,
            StageGroupType stageGroup,
            DifficultyLevelType difficultyLevel,
            int questionCount,
            CompletedStatus completedStatus) {
        private StageResponse toResponse() {
            return new StageResponse(
                    id.toString(),
                    stageImage.code(),
                    title,
                    description,
                    stageGroup,
                    difficultyLevel,
                    questionCount,
                    completedStatus);
        }
    }
}
