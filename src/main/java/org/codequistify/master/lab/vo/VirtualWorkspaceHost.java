package org.codequistify.master.domain.lab.vo;

public record VirtualWorkspaceHost(String value) {
    private static final String LAB_HOST = "wss://lab.pol.or.kr";

    public VirtualWorkspaceHost {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("virtual workspace host must not be blank");
        }
    }

    public static VirtualWorkspaceHost lab() {
        return new VirtualWorkspaceHost(LAB_HOST);
    }

    public String accessUrl(VirtualWorkspaceQuery query) {
        return value + query.value();
    }
}
