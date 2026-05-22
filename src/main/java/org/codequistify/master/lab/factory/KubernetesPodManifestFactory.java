package org.codequistify.master.domain.lab.factory;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import java.util.Locale;
import org.codequistify.master.domain.lab.domain.WorkspaceRuntime;
import org.codequistify.master.stage.domain.StageImageType;
import org.springframework.stereotype.Component;

@Component
public class KubernetesPodManifestFactory {
    private static final Long ACTIVE_DEADLINE = 10_800L;

    public Pod create(WorkspaceRuntime runtime, int port) {
        StageImageType stageImage = runtime.image();
        String playerLabel = runtime.playerId().value().toLowerCase(Locale.ROOT);
        String stageLabel = stageImage.resourceCode();
        String stageIdLabel = runtime.stageId().toString();

        return new PodBuilder()
                .withNewMetadata()
                .withName(runtime.name().value())
                .addToLabels("app", "pol")
                .addToLabels("tire", "term")
                .addToLabels("player", playerLabel)
                .addToLabels("stage", stageLabel)
                .addToLabels("stage-id", stageIdLabel)
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName(stageLabel)
                .withImage(stageImage.imageName())
                .addNewPort()
                .withContainerPort(port)
                .endPort()
                .withNewReadinessProbe() // agent 준비 확인
                .withNewHttpGet()
                .withPath("/health")
                .withPort(new IntOrString(8080))
                .endHttpGet()
                .withInitialDelaySeconds(9)
                .withPeriodSeconds(2)
                .endReadinessProbe()
                .endContainer()
                .withActiveDeadlineSeconds(ACTIVE_DEADLINE)
                .endSpec()
                .build();
    }
}
