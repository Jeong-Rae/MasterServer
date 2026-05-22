package org.codequistify.master.stage.dto;

import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.codequistify.master.stage.domain.CompletedStatus;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.StageGroupType;

@Data
public class StageSearchRequest {
    @Positive(message = "4104")
    private int pageIndex = 1;

    @Positive(message = "4104")
    private int pageSize = 10;

    private List<StageGroupType> stageGroupTypes = new ArrayList<>();

    private List<DifficultyLevelType> difficultyLevels = new ArrayList<>();

    private CompletedStatus completedStatus;

    private String searchText;
}
