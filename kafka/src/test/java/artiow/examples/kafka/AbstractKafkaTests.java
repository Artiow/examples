package artiow.examples.kafka;

import artiow.examples.kafka.utils.ContainerEvent;
import artiow.examples.kafka.utils.InterceptableContainer;
import artiow.examples.kafka.utils.TestcontainersUtils;
import java.util.Objects;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;

public abstract class AbstractKafkaTests {

    protected static final Network NETWORK = Network.newNetwork();

    @SuppressWarnings("resource")
    @Container
    protected static final GenericContainer<?> ZOOKEEPER =
        new GenericContainer<>("zookeeper")
            .withNetwork(NETWORK)
            .withNetworkAliases("zookeeper")
            .withExposedPorts(2181)
            .withLogConsumer(TestcontainersUtils.slf4jLogConsumer("zookeeper"))
            .waitingFor(Wait.forLogMessage(".* INFO\\s*\\[main:.*\\.Server@\\d*\\] - Started @\\d*ms\\n", 1));

    @SuppressWarnings("resource")
    @Container
    protected static final InterceptableContainer<?> KAFKA =
        new InterceptableContainer<>("bitnami/kafka")
            .dependsOn(ZOOKEEPER)
            .withNetwork(NETWORK)
            .withNetworkAliases("kafka")
            .withExposedPorts(9092)
            .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh"))
            .withCommand("-c", "while [ ! -f /opt/entrypoint.sh ]; do sleep 0.1; done; /opt/entrypoint.sh")
            .withInterceptorOn(ContainerEvent.STARTING, (container, containerInfo) -> {
                Objects.requireNonNull(containerInfo, "container info is null");
                final String internal = containerInfo.getConfig().getHostName() + ":" + "49092";
                final String external = container.getHost() + ":" + container.getMappedPort(9092);
                final String starterScript = "#!/bin/bash\n"
                    // setting env
                    + "export KAFKA_CFG_ADVERTISED_LISTENERS=" + "INTERNAL://" + internal + ",EXTERNAL://" + external + "\n"
                    // source ENTRYPOINT and CMD
                    + "/opt/bitnami/scripts/kafka/entrypoint.sh /opt/bitnami/scripts/kafka/run.sh\n";
                // noinspection OctalInteger
                container.copyFileToContainer(Transferable.of(starterScript, 0755), "/opt/entrypoint.sh");
            })
            .withEnv("KAFKA_CFG_LISTENERS", "INTERNAL://:49092,EXTERNAL://:9092")
            .withEnv("KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP", "INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT")
            .withEnv("KAFKA_CFG_INTER_BROKER_LISTENER_NAME", "INTERNAL")
            .withEnv("KAFKA_CFG_ZOOKEEPER_CONNECT", "zookeeper:2181")
            .withLogConsumer(TestcontainersUtils.slf4jLogConsumer("bitnami/kafka"))
            .waitingFor(Wait.forLogMessage(".* INFO\\s*\\[KafkaServer id=\\d*\\] started \\(kafka\\.server\\.KafkaServer\\)\\n", 1));


    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.zookeeper.connect-string", TestcontainersUtils.newSocketSupplier(ZOOKEEPER, 2181));
        registry.add("spring.kafka.bootstrap-servers", TestcontainersUtils.newSocketSupplier(KAFKA, 9092));
    }
}
