package org.codequistify.master.domain.lab.dto;

public record VirtualWorkspaceExistsResponse(String uid, String stageId, String stageCode, boolean exists) {}
