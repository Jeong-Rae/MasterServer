package org.codequistify.master.domain.lab.vo;

public record KubernetesNamespace(String value) {
    private static final String DEFAULT_NAMESPACE = "default";

    public KubernetesNamespace {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("kubernetes namespace must not be blank");
        }
    }

    public static KubernetesNamespace defaultNamespace() {
        return new KubernetesNamespace(DEFAULT_NAMESPACE);
    }
}
