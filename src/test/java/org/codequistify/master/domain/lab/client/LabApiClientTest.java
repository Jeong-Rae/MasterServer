package org.codequistify.master.domain.lab.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.shared.http.WebRequestClient;
import org.codequistify.master.shared.response.SuccessResponse;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.dto.StageQuestionActionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class LabApiClientTest {

    @Mock
    private WebRequestClient webRequestClient;

    private LabApiClient labApiClient;

    @BeforeEach
    void 테스트_대상을_준비한다() {
        labApiClient = new LabApiClient(webRequestClient);
    }

    @Test
    void Grade_요청은_stageCode를_소문자로_보내고_SuccessResponse를_반환한다() {
        when(webRequestClient.postForEntity(any(String.class), any(HttpEntity.class), any()))
                .thenReturn(ResponseEntity.ok(new SuccessResponse(true)));

        ResponseEntity<SuccessResponse> result = labApiClient.sendGradeRequest(
                StageImageType.S1001, new PlayerId("player-id"), new StageQuestionActionRequest("StageA", 1));

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity<StageQuestionActionRequest>> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        org.mockito.Mockito.verify(webRequestClient)
                .postForEntity(
                        urlCaptor.capture(),
                        entityCaptor.capture(),
                        org.mockito.ArgumentMatchers.eq(SuccessResponse.class));
        assertThat(result.getBody().success()).isTrue();
        assertThat(urlCaptor.getValue()).isEqualTo("https://lab.pol.or.kr/grade?uid=player-id&stage=s1001");
        assertThat(entityCaptor.getValue().getBody().stageCode()).isEqualTo("stagea");
    }

    @Test
    void Lab_API가_5xx로_응답하면_FAIL_PROCEED를_던진다() {
        when(webRequestClient.postForEntity(any(String.class), any(HttpEntity.class), any()))
                .thenReturn(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SuccessResponse(false)));

        assertThatThrownBy(() -> labApiClient.sendComposeRequest(
                        StageImageType.S1001, new PlayerId("player-id"), new StageQuestionActionRequest("StageA", 1)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FAIL_PROCEED);
    }
}
