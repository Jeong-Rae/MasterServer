package org.codequistify.master.stage.service.impl;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.service.LabAssignmentService;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.player.service.PlayerProfileService;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.util.SuccessResponse;
import org.codequistify.master.stage.domain.PlayerStageSolving;
import org.codequistify.master.stage.domain.Question;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.domain.answer.Answer;
import org.codequistify.master.stage.domain.vo.QuestionId;
import org.codequistify.master.stage.domain.vo.QuestionIndex;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.domain.vo.StageQuestions;
import org.codequistify.master.stage.domain.vo.SubmittedAnswer;
import org.codequistify.master.stage.dto.QuestionGradingRequest;
import org.codequistify.master.stage.dto.QuestionGradingResponse;
import org.codequistify.master.stage.dto.StageQuestionCreateRequest;
import org.codequistify.master.stage.dto.StageQuestionActionRequest;
import org.codequistify.master.stage.dto.StageCompletionResponse;
import org.codequistify.master.stage.dto.StageCreateRequest;
import org.codequistify.master.stage.repository.QuestionAttemptRepository;
import org.codequistify.master.stage.repository.QuestionRepository;
import org.codequistify.master.stage.repository.StageProgressRepository;
import org.codequistify.master.stage.repository.StageRepository;
import org.codequistify.master.stage.service.StageManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StageManagementServiceImpl implements StageManagementService {
    private final StageRepository stageRepository;
    private final QuestionRepository questionRepository;
    private final StageProgressRepository stageProgressRepository;
    private final QuestionAttemptRepository questionAttemptRepository;

    private final LabAssignmentService labAssignmentService;
    private final PlayerProfileService playerProfileService;
    private final PlayerDetailsService playerDetailsService;

    private final Logger LOGGER = LoggerFactory.getLogger(StageManagementServiceImpl.class);
    private static final int LEVEL_STEP_SIZE = 500;

    @Override
    @Transactional
    public void saveStage(StageCreateRequest request) {
        Stage stage = Stage.builder()
                .id(StageId.newId())
                .title(request.title())
                .description(request.description())
                .stageGroup(request.stageGroup())
                .difficultyLevel(request.difficultyLevel())
                .stageImage(StageImageType.S0000)
                .questions(StageQuestions.of(
                        request.questions().stream().map(this::toQuestion).toList()))
                .build();

        stageRepository.save(stage);
    }

    private Question toQuestion(StageQuestionCreateRequest request) {
        return Question.builder()
                .title(request.title())
                .description(request.description())
                .answer(Answer.of(request.answerType(), request.correctAnswer(), request.options(), false))
                .build();
    }

    @Override
    @Transactional
    public QuestionGradingResponse evaluateAnswer(PlayerPrincipal principal, QuestionGradingRequest request) {
        StageId stageId = StageId.of(request.stageId());
        QuestionIndex questionIndex = QuestionIndex.of(request.questionIndex());
        Question question = findQuestion(stageId, questionIndex);

        SubmittedAnswer submittedAnswer = SubmittedAnswer.of(request.submittedAnswer());
        boolean correct = question.isPractical()
                ? evaluatePracticalAnswerCorrectness(principal, stageId, questionIndex)
                : question.grade(submittedAnswer);

        questionAttemptRepository.record(principal.id(), question, submittedAnswer, correct);

        QuestionIndex nextIndex = questionIndex.next();
        boolean last = !questionRepository.existsById(QuestionId.of(stageId, nextIndex));
        boolean nextRequiresCompose = !last && findQuestion(stageId, nextIndex).requiresCompose();

        return new QuestionGradingResponse(correct, last ? -1 : nextIndex.value(), last, nextRequiresCompose);
    }

    private Question findQuestion(StageId stageId, QuestionIndex questionIndex) {
        return questionRepository
                .findById(QuestionId.of(stageId, questionIndex))
                .orElseThrow(() -> {
                    LOGGER.info(
                            "[findQuestion] {}, id: {}, index: {}",
                            ErrorCode.QUESTION_NOT_FOUND.getMessage(),
                            stageId.value(),
                            questionIndex.value());
                    return new BusinessException(ErrorCode.QUESTION_NOT_FOUND, HttpStatus.NOT_FOUND);
                });
    }

    private boolean evaluatePracticalAnswerCorrectness(
            PlayerPrincipal principal, StageId stageId, QuestionIndex questionIndex) {
        Stage stage = findStage(stageId);
        StageQuestionActionRequest actionRequest = StageQuestionActionRequest.from(stage.environment(), questionIndex);

        SuccessResponse response = labAssignmentService
                .sendGradingRequest(stage.stageImage(), principal.id(), actionRequest)
                .getBody();

        return response.success();
    }

    @Override
    @Transactional
    public SuccessResponse composeVirtualWorkspace(PlayerPrincipal principal, QuestionGradingRequest request) {
        StageId stageId = StageId.of(request.stageId());
        QuestionIndex questionIndex = QuestionIndex.of(request.questionIndex());
        Stage stage = findStage(stageId);

        StageQuestionActionRequest actionRequest = StageQuestionActionRequest.from(stage.environment(), questionIndex);
        return labAssignmentService
                .sendComposeRequest(stage.stageImage(), principal.id(), actionRequest)
                .getBody();
    }

    @Override
    @Transactional
    public StageCompletionResponse recordStageComplete(PlayerPrincipal principal, String stageIdValue) {
        StageId stageId = StageId.of(stageIdValue);
        Player player = playerDetailsService.findOnePlayerById(principal.id().value());
        Stage stage = findStage(stageId);

        PlayerStageSolving solving = stageProgressRepository
                .findByPlayerIdAndStageId(player.id(), stageId)
                .orElseGet(() -> PlayerStageSolving.start(player.id(), stageId));
        stageProgressRepository.save(
                solving.completeAt(QuestionIndex.of(stage.stageQuestions().count())));

        int updatedExp =
                playerProfileService.increaseExp(player, stage.difficultyLevel().exp());

        LOGGER.info("[recordStageComplete] player: {}, {} 클리어", player.id().value(), stageId.value());
        return new StageCompletionResponse(
                player.profile().experience().value(),
                player.profile().experience().value() / LEVEL_STEP_SIZE,
                updatedExp,
                updatedExp / LEVEL_STEP_SIZE);
    }

    @Override
    @Transactional
    public void recordInProgressStageInit(PlayerPrincipal principal, QuestionGradingRequest request) {
        StageId stageId = StageId.of(request.stageId());
        if (stageProgressRepository.existsByPlayerIdAndStageId(principal.id(), stageId)) {
            return;
        }

        findStage(stageId);
        stageProgressRepository.save(PlayerStageSolving.start(principal.id(), stageId));
        LOGGER.info(
                "[recordInProgressStageInit] 풀이 시작 기록 stage: {}, index: {}", stageId.value(), request.questionIndex());
    }

    @Override
    @Transactional
    public void updateInProgressStage(PlayerPrincipal principal, QuestionGradingRequest request) {
        StageId stageId = StageId.of(request.stageId());
        QuestionIndex questionIndex = QuestionIndex.of(request.questionIndex());
        PlayerStageSolving solving = stageProgressRepository
                .findByPlayerIdAndStageId(principal.id(), stageId)
                .orElseThrow(() -> {
                    LOGGER.info(
                            "[updateInProgressStage] {}, stage: {}",
                            ErrorCode.STAGE_PROGRESS_NOT_FOUND.getMessage(),
                            stageId.value());
                    return new BusinessException(ErrorCode.STAGE_PROGRESS_NOT_FOUND, HttpStatus.NOT_FOUND);
                });

        stageProgressRepository.save(solving.progressTo(questionIndex));
        LOGGER.info("[updateInProgressStage] 진행정도 업데이트 stage: {}, index: {}", stageId.value(), questionIndex.value());
    }

    private Stage findStage(StageId stageId) {
        return stageRepository.findById(stageId).orElseThrow(() -> {
            LOGGER.info("[findStage] {}, stage: {}", ErrorCode.STAGE_NOT_FOUND.getMessage(), stageId.value());
            return new BusinessException(ErrorCode.STAGE_NOT_FOUND, HttpStatus.NOT_FOUND);
        });
    }
}
