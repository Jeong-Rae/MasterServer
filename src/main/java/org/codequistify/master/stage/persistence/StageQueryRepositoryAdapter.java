package org.codequistify.master.stage.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.SolvingStatus;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPoint;
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
        BooleanBuilder where = stageWhere(searchRequest, stage, progress, playerId);

        List<Tuple> rows = queryFactory
                .select(
                        stage.id,
                        stage.stageImage,
                        stage.title,
                        stage.description,
                        stage.stageGroup,
                        stage.difficultyLevel,
                        stage.questionCount,
                        progress.status)
                .from(stage)
                .leftJoin(progress)
                .on(progress.id.stageId.eq(stage.id).and(progress.id.playerId.eq(playerId.value())))
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(stage.count())
                .from(stage)
                .leftJoin(progress)
                .on(progress.id.stageId.eq(stage.id).and(progress.id.playerId.eq(playerId.value())))
                .where(where)
                .fetchOne();

        List<StageResponse> responses = rows.stream()
                .map(row -> new StageResponse(
                        row.get(stage.id),
                        row.get(stage.stageImage).code(),
                        row.get(stage.title),
                        row.get(stage.description),
                        row.get(stage.stageGroup),
                        row.get(stage.difficultyLevel),
                        row.get(stage.questionCount),
                        completedStatusOf(row.get(progress.status))))
                .toList();

        return new PageImpl<>(responses, pageable, total == null ? 0 : total);
    }

    @Override
    public List<StageCodeResponse> findStageCodesByProgressStatus(PlayerId playerId, CompletedStatus status) {
        QStageEntity stage = QStageEntity.stageEntity;
        QPlayerStageProgressEntity progress = QPlayerStageProgressEntity.playerStageProgressEntity;

        return queryFactory
                .select(stage.stageImage)
                .from(progress)
                .join(stage)
                .on(stage.id.eq(progress.id.stageId))
                .where(progress.id.playerId.eq(playerId.value()).and(progress.status.eq(solvingStatusOf(status))))
                .fetch()
                .stream()
                .map(StageCodeResponse::of)
                .toList();
    }

    @Override
    public List<StageAttemptHeatMapPoint> countAttemptByDate(PlayerId playerId) {
        QQuestionAttemptEntity attempt = QQuestionAttemptEntity.questionAttemptEntity;
        DateExpression<LocalDate> attemptedDate =
                Expressions.dateTemplate(LocalDate.class, "date({0})", attempt.attemptedAt);

        return queryFactory
                .select(attemptedDate, attempt.count())
                .from(attempt)
                .where(attempt.playerId.eq(playerId.value()))
                .groupBy(attemptedDate)
                .fetch()
                .stream()
                .map(row -> new StageAttemptHeatMapPoint(row.get(attemptedDate), row.get(attempt.count())))
                .toList();
    }

    @Override
    public Optional<StageResponse> findPreviewBySearchText(String query) {
        QStageEntity stage = QStageEntity.stageEntity;
        return Optional.ofNullable(queryFactory
                        .select(
                                stage.id,
                                stage.stageImage,
                                stage.title,
                                stage.description,
                                stage.stageGroup,
                                stage.difficultyLevel,
                                stage.questionCount)
                        .from(stage)
                        .where(stage.status
                                .eq(StageStatus.APPROVED)
                                .and(stage.title
                                        .containsIgnoreCase(query)
                                        .or(stage.description.containsIgnoreCase(query))
                                        .or(stage.stageImage.stringValue().containsIgnoreCase(query))))
                        .limit(1)
                        .fetchOne())
                .map(row -> new StageResponse(
                        row.get(stage.id),
                        row.get(stage.stageImage).code(),
                        row.get(stage.title),
                        row.get(stage.description),
                        row.get(stage.stageGroup),
                        row.get(stage.difficultyLevel),
                        row.get(stage.questionCount),
                        CompletedStatus.NOT_COMPLETED));
    }

    private BooleanBuilder stageWhere(
            StageSearchRequest searchRequest, QStageEntity stage, QPlayerStageProgressEntity progress, PlayerId playerId) {
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
        return where.and(progress.id.playerId.eq(playerId.value()))
                .and(progress.status.eq(solvingStatusOf(searchRequest.getCompletedStatus())));
    }

    private CompletedStatus completedStatusOf(SolvingStatus status) {
        if (status == SolvingStatus.COMPLETED) {
            return CompletedStatus.COMPLETED;
        }
        if (status == SolvingStatus.IN_PROGRESS) {
            return CompletedStatus.IN_PROGRESS;
        }
        return CompletedStatus.NOT_COMPLETED;
    }

    private SolvingStatus solvingStatusOf(CompletedStatus status) {
        if (status == CompletedStatus.COMPLETED) {
            return SolvingStatus.COMPLETED;
        }
        return SolvingStatus.IN_PROGRESS;
    }
}
