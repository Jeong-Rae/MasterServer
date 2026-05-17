package org.codequistify.master.domain.lab.domain;

import java.time.LocalDateTime;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceEventId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;

public class VirtualWorkspaceStateChangeEvent {
    private final VirtualWorkspaceEventId id;
    private final VirtualWorkspaceId workspaceId;
    private final PlayerId playerId;
    private final VirtualWorkspaceStage stage;
    private final VirtualWorkspaceEventType event;
    private final VirtualWorkspaceStatus fromStatus;
    private final VirtualWorkspaceStatus toStatus;
    private final LocalDateTime occurredAt;
    private final VirtualWorkspaceEventResult result;
    private final String idempotencyKey;
    private final String failureReason;

    public VirtualWorkspaceStateChangeEvent(
            VirtualWorkspaceEventId id,
            VirtualWorkspaceId workspaceId,
            PlayerId playerId,
            VirtualWorkspaceStage stage,
            VirtualWorkspaceEventType event,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus,
            LocalDateTime occurredAt,
            VirtualWorkspaceEventResult result,
            String idempotencyKey,
            String failureReason) {
        if (id == null
                || workspaceId == null
                || playerId == null
                || stage == null
                || event == null
                || toStatus == null
                || occurredAt == null
                || result == null) {
            throw new IllegalArgumentException("virtual workspace state change event fields must not be null");
        }
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException(
                    "virtual workspace state change event idempotency key must not be blank");
        }
        this.id = id;
        this.workspaceId = workspaceId;
        this.playerId = playerId;
        this.stage = stage;
        this.event = event;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.occurredAt = occurredAt;
        this.result = result;
        this.idempotencyKey = idempotencyKey;
        this.failureReason = failureReason;
    }

    public static VirtualWorkspaceStateChangeEvent success(
            VirtualWorkspaceId workspaceId,
            PlayerId playerId,
            VirtualWorkspaceStage stage,
            VirtualWorkspaceEventType event,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus,
            LocalDateTime occurredAt,
            String idempotencyKey) {
        return new VirtualWorkspaceStateChangeEvent(
                VirtualWorkspaceEventId.newId(),
                workspaceId,
                playerId,
                stage,
                event,
                fromStatus,
                toStatus,
                occurredAt,
                VirtualWorkspaceEventResult.SUCCESS,
                idempotencyKey,
                null);
    }

    public static VirtualWorkspaceStateChangeEvent failed(
            VirtualWorkspaceId workspaceId,
            PlayerId playerId,
            VirtualWorkspaceStage stage,
            VirtualWorkspaceEventType event,
            VirtualWorkspaceStatus fromStatus,
            VirtualWorkspaceStatus toStatus,
            LocalDateTime occurredAt,
            String idempotencyKey,
            String failureReason) {
        return new VirtualWorkspaceStateChangeEvent(
                VirtualWorkspaceEventId.newId(),
                workspaceId,
                playerId,
                stage,
                event,
                fromStatus,
                toStatus,
                occurredAt,
                VirtualWorkspaceEventResult.FAILED,
                idempotencyKey,
                failureReason);
    }

    public VirtualWorkspaceEventId id() {
        return id;
    }

    public VirtualWorkspaceId workspaceId() {
        return workspaceId;
    }

    public PlayerId playerId() {
        return playerId;
    }

    public VirtualWorkspaceStage stage() {
        return stage;
    }

    public VirtualWorkspaceEventType event() {
        return event;
    }

    public VirtualWorkspaceStatus fromStatus() {
        return fromStatus;
    }

    public VirtualWorkspaceStatus toStatus() {
        return toStatus;
    }

    public LocalDateTime occurredAt() {
        return occurredAt;
    }

    public VirtualWorkspaceEventResult result() {
        return result;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    public String failureReason() {
        return failureReason;
    }
}
