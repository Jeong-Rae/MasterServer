package org.codequistify.master.domain.lab.factory;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import java.util.Locale;
import org.codequistify.master.domain.lab.domain.WorkspaceBinding;
import org.codequistify.master.stage.domain.StageImageType;
import org.springframework.stereotype.Component;

@Component
public class KubernetesServiceManifestFactory {
    public Service create(WorkspaceBinding binding, int port) {
        StageImageType stageImage = binding.image();
        String playerLabel = binding.playerId().value().toLowerCase(Locale.ROOT);
        String stageLabel = stageImage.resourceCode();
        String stageIdLabel = binding.stageId().toString();

        return new ServiceBuilder()
                .withNewMetadata()
                .withName(binding.name().value())
                .addToLabels("app", "pol")
                .addToLabels("tire", "term")
                .addToLabels("player", playerLabel)
                .addToLabels("stage", stageLabel)
                .addToLabels("stage-id", stageIdLabel)
                .endMetadata()
                .withNewSpec()
                .withType("ClusterIP")
                .addNewPort()
                .withName("http")
                .withProtocol("TCP")
                .withPort(port)
                .withTargetPort(new IntOrString(port))
                .endPort()
                .addToSelector("app", "pol")
                .addToSelector("tire", "term")
                .addToSelector("player", playerLabel)
                .addToSelector("stage", stageLabel)
                .addToSelector("stage-id", stageIdLabel)
                .endSpec()
                .build();
    }
}
