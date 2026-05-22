package org.codequistify.master.domain.player.dto;

import java.util.List;
import org.codequistify.master.stage.dto.StageCodeResponse;

public record PlayerStageProgressResponse(List<StageCodeResponse> stageCodes) {}
