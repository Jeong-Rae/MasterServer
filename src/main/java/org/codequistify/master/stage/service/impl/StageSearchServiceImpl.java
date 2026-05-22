package org.codequistify.master.stage.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.dto.PlayerStageProgressResponse;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.global.aspect.LogMonitoring;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.answer.OptionSelectable;
import org.codequistify.master.stage.domain.vo.QuestionId;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.dto.StageAttemptHeatMapPoint;
import org.codequistify.master.stage.dto.PageMetadata;
import org.codequistify.master.stage.dto.StageQuestionResponse;
import org.codequistify.master.stage.dto.StageSearchRequest;
import org.codequistify.master.stage.dto.StagePageResponse;
import org.codequistify.master.stage.dto.StageResponse;
import org.codequistify.master.stage.repository.QuestionRepository;
import org.codequistify.master.stage.repository.StageQueryRepository;
import org.codequistify.master.stage.repository.StageRepository;
import org.codequistify.master.stage.service.StageSearchService;
import org.codequistify.master.stage.utils.HangulExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StageSearchServiceImpl implements StageSearchService {
    private final StageRepository stageRepository;
    private final QuestionRepository questionRepository;
    private final StageQueryRepository stageQueryRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(StageSearchServiceImpl.class);

    @Override
    @Transactional(readOnly = true)
    public Stage getStageById(String stageId) {
        return stageRepository.findById(StageId.of(stageId)).orElseThrow(() -> {
            LOGGER.info("[findStageById] 등록되지 않은 스테이지 id: {}", stageId);
            return new BusinessException(ErrorCode.STAGE_NOT_FOUND, HttpStatus.NOT_FOUND);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public StageQuestionResponse findQuestion(String stageId, int questionIndex) {
        Question question = questionRepository
                .findById(QuestionId.of(StageId.of(stageId), QuestionIndex.of(questionIndex)))
                .orElseThrow(() -> {
                    LOGGER.info(
                            "[findQuestion] {}, id: {}, index: {}",
                            ErrorCode.QUESTION_NOT_FOUND.getMessage(),
                            stageId,
                            questionIndex);
                    return new BusinessException(ErrorCode.QUESTION_NOT_FOUND, HttpStatus.NOT_FOUND);
                });

        LOGGER.info("[findQuestion] 문항 조회, id: {}, index: {}", stageId, questionIndex);
        return questionResponseOf(question);
    }

    private StageQuestionResponse questionResponseOf(Question question) {
        List<String> options = question.answer() instanceof OptionSelectable optionSelectable
                ? optionSelectable.options().rawValues()
                : List.of();
        return new StageQuestionResponse(
                question.id().stageId().value(),
                question.index().value(),
                question.title(),
                question.description(),
                question.answer().type(),
                options);
    }

    @Override
    @Transactional(readOnly = true)
    public StageResponse getStageByChoCho(String query) {
        HangulExtractor hangulExtractor = new HangulExtractor();
        String choSrc = hangulExtractor.extractChoseongs(query);

        return stageRepository.findAll().stream()
                .parallel()
                .filter(stage -> hangulExtractor.containsByChoseong(stage.title(), choSrc)
                        || hangulExtractor.containsByChoseong(stage.description(), choSrc))
                .findAny()
                .map(stage -> StageResponse.from(stage, CompletedStatus.NOT_COMPLETED))
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public StageResponse getStageBySearchText(String query) {
        return stageQueryRepository.findPreviewBySearchText(query).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerStageProgressResponse getCompletedStagesByPlayerId(String playerId) {
        return new PlayerStageProgressResponse(
                stageQueryRepository.findStageCodesByProgressStatus(new PlayerId(playerId), CompletedStatus.COMPLETED));
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerStageProgressResponse getInProgressStagesByPlayerId(String playerId) {
        return new PlayerStageProgressResponse(stageQueryRepository.findStageCodesByProgressStatus(
                new PlayerId(playerId), CompletedStatus.IN_PROGRESS));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StageAttemptHeatMapPoint> getHeatMapDataPointsByModifiedDate(String playerId) {
        return stageQueryRepository.countAttemptByDate(new PlayerId(playerId));
    }

    @Override
    @Transactional(readOnly = true)
    @LogMonitoring
    public StagePageResponse findStagesByCriteria(StageSearchRequest searchRequest, PlayerPrincipal principal) {
        PageRequest pageRequest = PageRequest.of(searchRequest.getPageIndex() - 1, searchRequest.getPageSize());
        Page<StageResponse> pages =
                stageQueryRepository.findStagesByCriteria(searchRequest, principal.id(), pageRequest);
        PageMetadata pageMetadata = PageMetadata.of(
                pages.getTotalPages(), pages.getSize(), pages.getNumber() + 1, pages.getNumberOfElements(), (int)
                        pages.getTotalElements());

        LOGGER.info("[findStagesByCriteria] page 조회");
        return StagePageResponse.of(pages.getContent(), pageMetadata);
    }
}
