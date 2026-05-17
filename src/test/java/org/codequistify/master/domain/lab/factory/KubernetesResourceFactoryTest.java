package org.codequistify.master.domain.lab.factory;

import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import org.codequistify.master.domain.lab.domain.WorkspaceBinding;
import org.codequistify.master.domain.lab.domain.WorkspaceRuntime;
import org.codequistify.master.domain.lab.domain.vo.VirtualWorkspaceStage;
import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;

class KubernetesResourceFactoryTest {

    @Test
    void Pod는_VirtualWorkspace_리소스_이름과_stage_label을_사용한다() {
        KubernetesPodManifestFactory factory = new KubernetesPodManifestFactory();
        WorkspaceRuntime runtime =
                WorkspaceRuntime.from(new VirtualWorkspaceStage(1L, StageImageType.S1001), new PlayerId("Player-ID"));

        Pod pod = factory.create(runtime, 8080);

        assertThat(pod.getMetadata().getName()).isEqualTo("s1001-player-id");
        assertThat(pod.getMetadata().getLabels())
                .containsEntry("player", "player-id")
                .containsEntry("stage", "s1001")
                .containsEntry("stage-id", "1");
        assertThat(pod.getSpec().getContainers().get(0).getName()).isEqualTo("s1001");
    }

    @Test
    void Service는_VirtualWorkspace_리소스_이름과_selector를_사용한다() {
        KubernetesServiceManifestFactory factory = new KubernetesServiceManifestFactory();
        WorkspaceBinding binding =
                WorkspaceBinding.from(new VirtualWorkspaceStage(1L, StageImageType.S1001), new PlayerId("Player-ID"));

        Service service = factory.create(binding, 8080);

        assertThat(service.getMetadata().getName()).isEqualTo("s1001-player-id");
        assertThat(service.getMetadata().getLabels())
                .containsEntry("player", "player-id")
                .containsEntry("stage", "s1001")
                .containsEntry("stage-id", "1");
        assertThat(service.getSpec().getSelector())
                .containsEntry("player", "player-id")
                .containsEntry("stage", "s1001")
                .containsEntry("stage-id", "1");
    }
}
