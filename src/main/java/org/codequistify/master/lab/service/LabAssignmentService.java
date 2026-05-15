package org.codequistify.master.domain.lab.service;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.client.LabApiClient;
import org.codequistify.master.domain.stage.dto.StageActionRequest;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.util.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LabAssignmentService {
    private final LabApiClient labApiClient;
    private final Logger LOGGER = LoggerFactory.getLogger(LabAssignmentService.class);

    @LogExecutionTime
    public ResponseEntity<SuccessResponse> sendGradingRequest(String stageCode, String uid, StageActionRequest request) {
        LOGGER.info("Request Data: {}", request);
        return labApiClient.sendGradeRequest(stageCode, uid, request);
    }

    @LogExecutionTime
    public ResponseEntity<SuccessResponse> sendComposeRequest(String stageCode, String uid, StageActionRequest request) {
        return labApiClient.sendComposeRequest(stageCode, uid, request);
    }
}
