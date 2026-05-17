package org.codequistify.master.domain.lab.domain;

import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;

public class VirtualWorkspace {
    private final VirtualWorkspaceId id;
    private final PlayerId playerId;
    private final VirtualWorkspaceStage stage;
    private VirtualWorkspaceStatus status;
    private String failureReason;

    public VirtualWorkspace(
            VirtualWorkspaceId id,
            PlayerId playerId,
            VirtualWorkspaceStage stage,
            VirtualWorkspaceStatus status,
            String failureReason) {
        if (id == null || playerId == null || stage == null || status == null) {
            throw new IllegalArgumentException("virtual workspace fields must not be null");
        }
        this.id = id;
        this.playerId = playerId;
        this.stage = stage;
        this.status = status;
        this.failureReason = failureReason;
    }

    public static VirtualWorkspace startFor(PlayerId playerId, VirtualWorkspaceStage stage) {
        return new VirtualWorkspace(
                VirtualWorkspaceId.newId(), playerId, stage, VirtualWorkspaceStatus.REQUESTED, null);
    }

    public VirtualWorkspace startProvisioning() {
        transit(VirtualWorkspaceStatus.REQUESTED, VirtualWorkspaceStatus.PROVISIONING);
        return this;
    }

    public VirtualWorkspace markRunning() {
        transit(VirtualWorkspaceStatus.PROVISIONING, VirtualWorkspaceStatus.RUNNING);
        return this;
    }

    public VirtualWorkspace startDeleting() {
        if (status != VirtualWorkspaceStatus.REQUESTED
                && status != VirtualWorkspaceStatus.PROVISIONING
                && status != VirtualWorkspaceStatus.RUNNING
                && status != VirtualWorkspaceStatus.FAILED) {
            throw new IllegalStateException("virtual workspace cannot start deleting from " + status);
        }
        status = VirtualWorkspaceStatus.DELETING;
        return this;
    }

    public VirtualWorkspace markDeleted() {
        transit(VirtualWorkspaceStatus.DELETING, VirtualWorkspaceStatus.DELETED);
        return this;
    }

    public VirtualWorkspace markFailed(String reason) {
        if (status == VirtualWorkspaceStatus.DELETED) {
            throw new IllegalStateException("deleted virtual workspace cannot fail");
        }
        status = VirtualWorkspaceStatus.FAILED;
        failureReason = reason;
        return this;
    }

    public boolean isRunning() {
        return status == VirtualWorkspaceStatus.RUNNING;
    }

    public boolean isTerminal() {
        return status == VirtualWorkspaceStatus.DELETED || status == VirtualWorkspaceStatus.FAILED;
    }

    public WorkspaceRuntime runtime() {
        return WorkspaceRuntime.from(stage, playerId);
    }

    public WorkspaceBinding binding() {
        return WorkspaceBinding.from(stage, playerId);
    }

    public VirtualWorkspaceId id() {
        return id;
    }

    public PlayerId playerId() {
        return playerId;
    }

    public VirtualWorkspaceStage stage() {
        return stage;
    }

    public VirtualWorkspaceStatus status() {
        return status;
    }

    public String failureReason() {
        return failureReason;
    }

    private void transit(VirtualWorkspaceStatus from, VirtualWorkspaceStatus to) {
        if (status != from) {
            throw new IllegalStateException("virtual workspace cannot transition from " + status + " to " + to);
        }
        status = to;
    }
}
