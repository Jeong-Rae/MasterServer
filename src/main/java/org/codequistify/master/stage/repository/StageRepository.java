package org.codequistify.master.stage.repository;

import java.util.List;
import java.util.Optional;
import org.codequistify.master.stage.domain.Stage;
import org.codequistify.master.stage.domain.vo.StageId;

public interface StageRepository {
    Stage save(Stage stage);

    Optional<Stage> findById(StageId id);

    List<Stage> findAll();
}
