package org.codequistify.master.domain.lab.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.*;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.shared.persistence.BaseTimeEntity;
import org.codequistify.master.stage.domain.StageImageType;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "virtual_workspace",
        indexes = {
            @Index(name = "idx_virtual_workspace_stage_lookup", columnList = "player_id, stage_id, status, updated_at"),
            @Index(
                    name = "idx_virtual_workspace_image_lookup",
                    columnList = "player_id, stage_image, status, updated_at")
        })
public class VirtualWorkspaceEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "stage_id", nullable = false)
    private UUID stageId;

    @Column(name = "stage_image", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StageImageType stageImage;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private VirtualWorkspaceStatus status;

    @Column(name = "failure_reason")
    private String failureReason;
}
