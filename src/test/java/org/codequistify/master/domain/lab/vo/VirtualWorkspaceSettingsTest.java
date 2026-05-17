package org.codequistify.master.domain.lab.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.codequistify.master.domain.player.domain.vo.PlayerId;
import org.codequistify.master.domain.stage.domain.StageImageType;
import org.junit.jupiter.api.Test;

class VirtualWorkspaceSettingsTest {

    @Test
    void Stage와_Player_id로_K8s_리소스_이름을_소문자로_생성한다() {
        KubernetesResourceName resourceName =
                KubernetesResourceName.from(StageImageType.S1001, new PlayerId("Player-ID"));

        assertThat(resourceName.value()).isEqualTo("s1001-player-id");
    }

    @Test
    void 기본_K8s_namespace는_default이다() {
        KubernetesNamespace namespace = KubernetesNamespace.defaultNamespace();

        assertThat(namespace.value()).isEqualTo("default");
    }

    @Test
    void VirtualWorkspace_query는_uid와_stage를_포함한다() {
        VirtualWorkspaceQuery query = VirtualWorkspaceQuery.from(StageImageType.S1001, new PlayerId("Player-ID"));

        assertThat(query.value()).isEqualTo("?uid=player-id&stage=s1001");
    }

    @Test
    void VirtualWorkspace_host는_query를_붙인_접속_URL을_생성한다() {
        VirtualWorkspaceHost host = VirtualWorkspaceHost.lab();
        VirtualWorkspaceQuery query = VirtualWorkspaceQuery.from(StageImageType.S1001, new PlayerId("Player-ID"));

        assertThat(host.accessUrl(query)).isEqualTo("wss://lab.pol.or.kr?uid=player-id&stage=s1001");
    }

    @Test
    void Lab_API_endpoint는_path와_query를_결합한다() {
        LabApiEndpoint endpoint = LabApiEndpoint.lab();
        VirtualWorkspaceQuery query = VirtualWorkspaceQuery.from(StageImageType.S1001, new PlayerId("Player-ID"));

        assertThat(endpoint.url("/grade", query)).isEqualTo("https://lab.pol.or.kr/grade?uid=player-id&stage=s1001");
    }

    @Test
    void VO는_빈_값으로_생성할_수_없다() {
        assertThatThrownBy(() -> new KubernetesResourceName(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new KubernetesNamespace(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VirtualWorkspaceQuery(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new VirtualWorkspaceHost(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new LabApiEndpoint(" ")).isInstanceOf(IllegalArgumentException.class);
    }
}
