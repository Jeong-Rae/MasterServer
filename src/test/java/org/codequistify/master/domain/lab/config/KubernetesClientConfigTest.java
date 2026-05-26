package org.codequistify.master.domain.lab.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class KubernetesClientConfigTest {
    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner().withUserConfiguration(KubernetesClientConfig.class);

    @Test
    void 로컬_프로필로_기동하면_쿠버네티스_목_클라이언트를_사용한다() {
        contextRunner.withPropertyValues("spring.profiles.active=local").run(context -> {
            assertThat(context).hasSingleBean(KubernetesServer.class);
            assertThat(context).hasSingleBean(KubernetesClient.class);

            KubernetesServer kubernetesServer = context.getBean(KubernetesServer.class);
            KubernetesClient kubernetesClient = context.getBean(KubernetesClient.class);

            assertThat(kubernetesClient).isSameAs(kubernetesServer.getClient());
        });
    }
}
