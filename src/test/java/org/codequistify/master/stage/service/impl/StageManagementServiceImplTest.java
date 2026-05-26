package org.codequistify.master.stage.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.codequistify.master.domain.lab.service.LabAssignmentService;
import org.codequistify.master.domain.player.domain.Player;
import org.codequistify.master.domain.player.domain.authentication.PlayerAuthentication;
import org.codequistify.master.domain.player.domain.authorization.PlayerAuthorization;
import org.codequistify.master.domain.player.domain.profile.PlayerProfile;
import org.codequistify.master.domain.player.domain.vo.PlayerEmail;
import org.codequistify.master.domain.player.domain.vo.PlayerExperience;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.player.domain.vo.PlayerName;
import org.codequistify.master.domain.player.domain.vo.RawPassword;
import org.codequistify.master.domain.player.security.AuthenticatedPlayer;
import org.codequistify.master.domain.player.security.HashPasswordEncoder;
import org.codequistify.master.domain.player.security.PlayerAuthorizationGuard;
import org.codequistify.master.domain.player.security.PlayerDetailsService;
import org.codequistify.master.domain.player.security.PlayerPrincipal;
import org.codequistify.master.domain.player.service.PlayerProfileService;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.stage.domain.AnswerType;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.StageGroupType;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.dto.StageCreateRequest;
import org.codequistify.master.stage.dto.StageQuestionCreateRequest;
import org.codequistify.master.stage.repository.QuestionAttemptRepository;
import org.codequistify.master.stage.repository.QuestionRepository;
import org.codequistify.master.stage.repository.StageProgressRepository;
import org.codequistify.master.stage.repository.StageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StageManagementServiceImplTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private StageProgressRepository stageProgressRepository;

    @Mock
    private QuestionAttemptRepository questionAttemptRepository;

    @Mock
    private LabAssignmentService labAssignmentService;

    @Mock
    private PlayerProfileService playerProfileService;

    @Mock
    private PlayerDetailsService playerDetailsService;

    @Test
    void 스테이지_생성은_관리자가_아니면_예외를_던진다() {
        StageManagementServiceImpl stageManagementService = stageManagementService();
        PlayerPrincipal principal = principal("player-id");
        AuthenticatedPlayer requester = AuthenticatedPlayer.from(principal);

        assertThatThrownBy(() -> stageManagementService.saveStage(requester, stageCreateRequest()))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_PLAYER_PERMISSION.getMessage());
        verifyNoInteractions(stageRepository);
    }

    @Test
    void 스테이지_완료는_자기_진행_기록이_없으면_예외를_던진다() {
        StageManagementServiceImpl stageManagementService = stageManagementService();
        PlayerPrincipal principal = principal("player-id");
        AuthenticatedPlayer requester = AuthenticatedPlayer.from(principal);
        StageId stageId = StageId.of("stage-id");
        Player player = player("player-id");
        Stage stage = Stage.builder()
                .id(stageId)
                .difficultyLevel(DifficultyLevelType.L1)
                .stageImage(StageImageType.S0000)
                .build();
        when(playerDetailsService.findOnePlayerById(requester.id().value())).thenReturn(player);
        when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));
        when(stageProgressRepository.findByPlayerIdAndStageId(player.id(), stageId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> stageManagementService.recordStageComplete(requester, stageId.value()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STAGE_PROGRESS_NOT_FOUND);
        verifyNoInteractions(playerProfileService);
    }

    private StageManagementServiceImpl stageManagementService() {
        return new StageManagementServiceImpl(
                stageRepository,
                questionRepository,
                stageProgressRepository,
                questionAttemptRepository,
                labAssignmentService,
                new PlayerAuthorizationGuard(),
                playerProfileService,
                playerDetailsService);
    }

    private PlayerPrincipal principal(String id) {
        return new PlayerPrincipal(new PlayerId(id), PlayerAuthorization.basicPlayer(), false);
    }

    private Player player(String id) {
        HashPasswordEncoder encoder = new HashPasswordEncoder();
        return Player.create(
                new PlayerId(id),
                new PlayerProfile(new PlayerName("pol"), new PlayerEmail("pol@example.com"), new PlayerExperience(0)),
                PlayerAuthentication.local(encoder.encode(RawPassword.middleLevel("password99763892*"))),
                PlayerAuthorization.basicPlayer());
    }

    private StageCreateRequest stageCreateRequest() {
        return new StageCreateRequest(
                "title",
                "description",
                StageGroupType.BASIC_PROBLEMS,
                DifficultyLevelType.L1,
                List.of(new StageQuestionCreateRequest(
                        "question", "description", AnswerType.SHORT_ANSWER, "answer", List.of())));
    }
}
