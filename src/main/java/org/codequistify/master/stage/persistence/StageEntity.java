package org.codequistify.master.stage.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.codequistify.master.global.uuid.UUIDv7Generator;
import org.codequistify.master.shared.persistence.BaseTimeEntity;
import org.codequistify.master.stage.domain.DifficultyLevelType;
import org.codequistify.master.stage.domain.StageGroupType;
import org.codequistify.master.stage.domain.StageImageType;

@Entity
@Table(
        name = "stage",
        indexes = {
            @Index(
                    name = "idx_stage_search_filter",
                    columnList = "status, stage_group, difficulty_level, created_at, id"),
            @Index(name = "idx_stage_default_list", columnList = "status, created_at, id")
        })
public class StageEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_group", nullable = false, length = 40)
    private StageGroupType stageGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 20)
    private DifficultyLevelType difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage_image", nullable = false, length = 40)
    private StageImageType stageImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StageStatus status;

    @Column(name = "question_count", nullable = false)
    private int questionCount;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();

    protected StageEntity() {}

    private StageEntity(
            UUID id,
            String title,
            String description,
            StageGroupType stageGroup,
            DifficultyLevelType difficultyLevel,
            StageImageType stageImage,
            StageStatus status) {
        this.id = id == null ? UUID.fromString(UUIDv7Generator.generate()) : id;
        this.title = title;
        this.description = description;
        this.stageGroup = stageGroup;
        this.difficultyLevel = difficultyLevel;
        this.stageImage = stageImage;
        this.status = status == null ? StageStatus.DRAFT : status;
    }

    public static StageEntity create(
            UUID id,
            String title,
            String description,
            StageGroupType stageGroup,
            DifficultyLevelType difficultyLevel,
            StageImageType stageImage,
            StageStatus status) {
        return new StageEntity(id, title, description, stageGroup, difficultyLevel, stageImage, status);
    }

    public void addQuestion(QuestionEntity question) {
        question.attachStage(this);
        questions.add(question);
        questionCount = questions.size();
    }

    public UUID id() {
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

    public StageStatus status() {
        return status;
    }

    public int questionCount() {
        return questionCount;
    }

    public List<QuestionEntity> questions() {
        return List.copyOf(questions);
    }
}
