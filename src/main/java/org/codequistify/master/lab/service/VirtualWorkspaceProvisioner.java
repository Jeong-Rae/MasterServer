package org.codequistify.master.domain.lab.service;

import org.codequistify.master.domain.lab.domain.VirtualWorkspace;

public interface VirtualWorkspaceProvisioner {
    void provision(VirtualWorkspace workspace);

    void remove(VirtualWorkspace workspace);

    boolean runtimeReady(VirtualWorkspace workspace);

    boolean runtimeExists(VirtualWorkspace workspace);

    boolean bindingExists(VirtualWorkspace workspace);
}
