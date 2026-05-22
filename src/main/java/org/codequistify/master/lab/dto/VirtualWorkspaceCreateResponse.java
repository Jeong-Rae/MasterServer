package org.codequistify.master.domain.lab.dto;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.vo.VirtualWorkspaceHost;
import org.codequistify.master.domain.lab.vo.VirtualWorkspaceQuery;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.stage.domain.StageImageType;

public record VirtualWorkspaceCreateResponse(String url, String query) {
    public static VirtualWorkspaceCreateResponse of(
            VirtualWorkspaceHost host, PlayerId playerId, StageImageType stageImage) {
        VirtualWorkspaceQuery query = VirtualWorkspaceQuery.from(stageImage, playerId);
        return new VirtualWorkspaceCreateResponse(host.accessUrl(query), query.value());
    }

    public static VirtualWorkspaceCreateResponse from(VirtualWorkspace workspace, VirtualWorkspaceHost host) {
        return of(host, workspace.playerId(), workspace.stage().image());
    }

    public static record XHeader(String key, String value) {}
}
