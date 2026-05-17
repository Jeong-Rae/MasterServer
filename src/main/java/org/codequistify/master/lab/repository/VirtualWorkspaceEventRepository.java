package org.codequistify.master.domain.lab.repository;

import java.util.List;
import org.codequistify.master.domain.lab.domain.VirtualWorkspaceStateChangeEvent;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceId;

public interface VirtualWorkspaceEventRepository {
    VirtualWorkspaceStateChangeEvent saveIfAbsent(VirtualWorkspaceStateChangeEvent event);

    List<VirtualWorkspaceStateChangeEvent> findAllByWorkspaceId(VirtualWorkspaceId workspaceId);
}
