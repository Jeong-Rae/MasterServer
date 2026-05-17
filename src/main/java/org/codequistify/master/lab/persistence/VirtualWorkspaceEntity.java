package org.codequistify.master.domain.lab.persistence;

import jakarta.persistence.*;
import lombok.*;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.codequistify.master.global.util.BaseTimeEntity;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "virtual_workspace",
        indexes = {@Index(name = "idx_virtual_workspace_lookup", columnList = "player_id, stage_id, status")})
public class VirtualWorkspaceEntity extends BaseTimeEntity {
    @Id
    @Column(name = "id", columnDefinition = "char(36)")
    private String id;

    @Column(name = "player_id", nullable = false, columnDefinition = "char(36)")
    private String playerId;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Column(name = "stage_image", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StageImageType stageImage;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private VirtualWorkspaceStatus status;

    @Column(name = "failure_reason")
    private String failureReason;
}
