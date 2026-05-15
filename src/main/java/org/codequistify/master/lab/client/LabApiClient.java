package org.codequistify.master.domain.lab.client;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.utils.KubernetesResourceNaming;
import org.codequistify.master.domain.stage.dto.StageActionRequest;
import org.codequistify.master.global.exception.ErrorCode;
import org.codequistify.master.global.exception.domain.BusinessException;
import org.codequistify.master.global.http.WebRequestClient;
import org.codequistify.master.global.util.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Component
@RequiredArgsConstructor
public class LabApiClient {
    private static final String LAB_API_URI = "https://lab.pol.or.kr";

    private final Logger LOGGER = LoggerFactory.getLogger(LabApiClient.class);
    private final WebRequestClient webRequestClient;

    public ResponseEntity<SuccessResponse> sendGradeRequest(String stageCode, String id, StageActionRequest request) {
        return sendRequest("/grade", stageCode, id, request);
    }

    public ResponseEntity<SuccessResponse> sendComposeRequest(String stageCode, String id, StageActionRequest request) {
        return sendRequest("/compose", stageCode, id, request);
    }

    private ResponseEntity<SuccessResponse> sendRequest(
            String path,
            String stageCode,
            String id,
            StageActionRequest request
    ) {
        String url = LAB_API_URI + path + KubernetesResourceNaming.getQuery(stageCode, id);
        StageActionRequest normalizedRequest = new StageActionRequest(request.stageCode().toLowerCase(), request.questionIndex());
        HttpEntity<StageActionRequest> entity = new HttpEntity<>(normalizedRequest, jsonHeaders());

        try {
            ResponseEntity<SuccessResponse> response = webRequestClient.postForEntity(url, entity, SuccessResponse.class);
            if (response.getStatusCode().is5xxServerError()) {
                LOGGER.info("[sendRequest] 실습서버가 정상적으로 응답하지 않습니다. url: {}", url);
                throw new BusinessException(ErrorCode.FAIL_PROCEED, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return response;
        } catch (HttpServerErrorException exception) {
            LOGGER.error("[sendRequest] Internal Server Error: {}, URL: {}", exception.getResponseBodyAsString(), url);
            throw exception;
        } catch (ResourceAccessException exception) {
            LOGGER.error("[sendRequest] Resource Access Error: {}, URL: {}", exception.getMessage(), url);
            throw exception;
        }
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
