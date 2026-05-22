package org.codequistify.master.domain.lab.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventResult;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceEventType;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStatus;
import org.codequistify.master.stage.domain.StageImageType;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "virtual_workspace_event_log",
        indexes = {
            @Index(name = "idx_virtual_workspace_event_workspace", columnList = "workspace_id, occurred_at"),
            @Index(name = "idx_virtual_workspace_event_lookup", columnList = "player_id, stage_id, occurred_at")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_virtual_workspace_event_idempotency", columnNames = "idempotency_key")
        })
public class VirtualWorkspaceEventEntity {
    @Id
    @Column(name = "id", columnDefinition = "char(36)")
    private String id;

    @Column(name = "workspace_id", nullable = false, columnDefinition = "char(36)")
    private String workspaceId;

    @Column(name = "player_id", nullable = false, columnDefinition = "char(36)")
    private String playerId;

    @Column(name = "stage_id", nullable = false, columnDefinition = "char(36)")
    private String stageId;

    @Column(name = "stage_image", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StageImageType stageImage;

    @Column(name = "event_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private VirtualWorkspaceEventType eventType;

    @Column(name = "from_status", length = 30)
    @Enumerated(EnumType.STRING)
    private VirtualWorkspaceStatus fromStatus;

    @Column(name = "to_status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private VirtualWorkspaceStatus toStatus;

    @Column(name = "result", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VirtualWorkspaceEventResult result;

    @Column(name = "occurred_at", nullable = false, columnDefinition = "DATETIME(0)")
    private LocalDateTime occurredAt;

    @Column(name = "idempotency_key", nullable = false, length = 180)
    private String idempotencyKey;

    @Column(name = "failure_reason")
    private String failureReason;
}
