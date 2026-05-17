package org.codequistify.master.domain.lab.service;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.codequistify.master.domain.lab.domain.VirtualWorkspace;
import org.codequistify.master.domain.lab.factory.KubernetesPodManifestFactory;
import org.codequistify.master.domain.lab.factory.KubernetesServiceManifestFactory;
import org.codequistify.master.domain.lab.vo.KubernetesNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class KubernetesVirtualWorkspaceProvisioner implements VirtualWorkspaceProvisioner {
    private static final KubernetesNamespace DEFAULT_NAMESPACE = KubernetesNamespace.defaultNamespace();

    private final Logger LOGGER = LoggerFactory.getLogger(KubernetesVirtualWorkspaceProvisioner.class);
    private final KubernetesPodManifestFactory podManifestFactory;
    private final KubernetesServiceManifestFactory serviceManifestFactory;
    private final KubernetesClient kubernetesClient;

    @Override
    public void provision(VirtualWorkspace workspace) {
        createBinding(workspace);
        createRuntime(workspace);
    }

    @Override
    public void remove(VirtualWorkspace workspace) {
        deleteRuntime(workspace);
        deleteBinding(workspace);
    }

    public Service createBinding(VirtualWorkspace workspace) {
        Service service = serviceManifestFactory.create(workspace.binding(), 8080);

        service = kubernetesClient
                .services()
                .inNamespace(DEFAULT_NAMESPACE.value())
                .resource(service)
                .create();

        LOGGER.debug("[createBinding] service: {}", service.getMetadata().getName());
        return service;
    }

    public Pod createRuntime(VirtualWorkspace workspace) {
        Pod pod = podManifestFactory.create(workspace.runtime(), 8080);

        pod = kubernetesClient
                .pods()
                .inNamespace(DEFAULT_NAMESPACE.value())
                .resource(pod)
                .create();

        LOGGER.debug("[createRuntime] pod: {}", pod.getMetadata().getName());
        return pod;
    }

    public void deleteBinding(VirtualWorkspace workspace) {
        deleteBinding(workspace.binding().name().value());
    }

    public void deleteBinding(String bindingName) {
        List<StatusDetails> result = kubernetesClient
                .services()
                .inNamespace(DEFAULT_NAMESPACE.value())
                .withName(bindingName)
                .delete();

        LOGGER.debug("[deleteBinding] result {}", result);
    }

    public void deleteRuntime(VirtualWorkspace workspace) {
        deleteRuntime(workspace.runtime().name().value());
    }

    public void deleteRuntime(String runtimeName) {
        List<StatusDetails> result = kubernetesClient
                .pods()
                .inNamespace(DEFAULT_NAMESPACE.value())
                .withName(runtimeName)
                .delete();

        LOGGER.debug("[deleteRuntime] result {}", result);
    }

    @Override
    public boolean bindingExists(VirtualWorkspace workspace) {
        return kubernetesClient
                        .services()
                        .inNamespace(DEFAULT_NAMESPACE.value())
                        .withName(workspace.binding().name().value())
                        .get()
                != null;
    }

    @Override
    public boolean runtimeExists(VirtualWorkspace workspace) {
        return kubernetesClient
                        .pods()
                        .inNamespace(DEFAULT_NAMESPACE.value())
                        .withName(workspace.runtime().name().value())
                        .get()
                != null;
    }

    @Override
    public boolean runtimeReady(VirtualWorkspace workspace) {
        Pod pod = kubernetesClient
                .pods()
                .inNamespace(DEFAULT_NAMESPACE.value())
                .withName(workspace.runtime().name().value())
                .get();

        if (pod == null || pod.getStatus() == null || pod.getStatus().getConditions() == null) {
            return false;
        }
        return pod.getStatus().getConditions().stream().anyMatch(this::ready);
    }

    public List<Pod> getTimeoutRuntimes() {
        PodList podList =
                kubernetesClient.pods().inNamespace(DEFAULT_NAMESPACE.value()).list();

        return podList.getItems().stream()
                .filter(this::isPhaseFailed)
                .filter(this::isReasonDeadlineExceeded)
                .filter(this::hasErrorStatus)
                .toList();
    }

    private boolean ready(PodCondition condition) {
        return "Ready".equals(condition.getType()) && "True".equals(condition.getStatus());
    }

    private boolean isPhaseFailed(Pod pod) {
        return "Failed".equals(pod.getStatus().getPhase());
    }

    private boolean isReasonDeadlineExceeded(Pod pod) {
        return "DeadlineExceeded".equals(pod.getStatus().getReason());
    }

    private boolean hasErrorStatus(Pod pod) {
        return pod.getStatus().getContainerStatuses().stream()
                .anyMatch(status -> status.getState().getTerminated() != null
                        && "Error".equals(status.getState().getTerminated().getReason()));
    }
}
