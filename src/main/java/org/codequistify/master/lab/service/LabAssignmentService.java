package org.codequistify.master.domain.lab.service;

import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.client.LabApiClient;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.global.aspect.LogExecutionTime;
import org.codequistify.master.global.util.SuccessResponse;
import org.codequistify.master.stage.domain.StageImageType;
import org.codequistify.master.stage.dto.StageQuestionActionRequest;
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
    public ResponseEntity<SuccessResponse> sendGradingRequest(
            StageImageType stageImage, PlayerId playerId, StageQuestionActionRequest request) {
        LOGGER.info("Request Data: {}", request);
        return labApiClient.sendGradeRequest(stageImage, playerId, request);
    }

    @LogExecutionTime
    public ResponseEntity<SuccessResponse> sendComposeRequest(
            StageImageType stageImage, PlayerId playerId, StageQuestionActionRequest request) {
        return labApiClient.sendComposeRequest(stageImage, playerId, request);
    }
}
