package org.codequistify.master.domain.lab.vo;

public record LabApiEndpoint(String value) {
    private static final String LAB_API_URI = "https://lab.pol.or.kr";

    public LabApiEndpoint {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("lab api endpoint must not be blank");
        }
    }

    public static LabApiEndpoint lab() {
        return new LabApiEndpoint(LAB_API_URI);
    }

    public String url(String path, VirtualWorkspaceQuery query) {
        return value + path + query.value();
    }
}
