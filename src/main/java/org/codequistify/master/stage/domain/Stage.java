package org.codequistify.master.stage.domain;

import lombok.Builder;
import lombok.ToString;
import org.codequistify.master.stage.domain.vo.StageEnvironment;
import org.codequistify.master.stage.domain.vo.StageId;
import org.codequistify.master.stage.domain.vo.StageQuestions;

@ToString
public class Stage {
    private final StageId id;
    private final String title;
    private final String description;
    private final StageGroupType stageGroup;
    private final DifficultyLevelType difficultyLevel;
    private final StageQuestions questions;
    private StageImageType stageImage;
    private boolean approved;

    @Builder
    public Stage(
            StageId id,
            String title,
            String description,
            StageGroupType stageGroup,
            DifficultyLevelType difficultyLevel,
            StageQuestions questions,
            StageImageType stageImage,
            boolean approved) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.stageGroup = stageGroup;
        this.difficultyLevel = difficultyLevel;
        this.questions = (questions == null ? StageQuestions.empty() : questions).assignIndexes();
        if (id != null) {
            this.questions.assignIds(id);
        }
        this.stageImage = stageImage;
        this.approved = approved;
    }

    public void updateStageImage(StageImageType stageImageType) {
        this.stageImage = stageImageType;
    }

    public StageEnvironment environment() {
        return StageEnvironment.from(this);
    }

    public StageId id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public StageGroupType stageGroup() {
        return stageGroup;
    }

    public DifficultyLevelType difficultyLevel() {
        return difficultyLevel;
    }

    public StageImageType stageImage() {
        return stageImage;
    }

    public boolean approved() {
        return approved;
    }

    public String stageCode() {
        return stageImage.code();
    }

    public boolean stageImageContains(String query) {
        return stageImage.contains(query);
    }

    public StageQuestions stageQuestions() {
        return questions;
    }
}
