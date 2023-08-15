package artiow.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class KafkaClientTests {

    static final Network NETWORK = Network.newNetwork();

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> ZOOKEEPER =
        new GenericContainer<>("zookeeper")
            .withNetwork(NETWORK)
            .withNetworkAliases("zookeeper")
            .withExposedPorts(2181)
            .withLogConsumer(TestcontainersUtils.slf4jLogConsumer("zookeeper"))
            .waitingFor(Wait.forLogMessage(".* INFO\\s*\\[main:.*\\.Server@\\d*\\] - Started @\\d*ms\\n", 1));

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> KAFKA =
        new GenericContainer<>("bitnami/kafka")
            .dependsOn(ZOOKEEPER)
            .withNetwork(NETWORK)
            .withNetworkAliases("kafka")
            .withExposedPorts(9092)
            .withEnv("KAFKA_CFG_ZOOKEEPER_CONNECT", "zookeeper:2181")
            .withLogConsumer(TestcontainersUtils.slf4jLogConsumer("bitnami/kafka"))
            .waitingFor(Wait.forLogMessage(".* INFO\\s*\\[KafkaServer id=\\d*\\] started \\(kafka\\.server\\.KafkaServer\\)\\n", 1));


    @Value("${spring.application.name}")
    String applicationName;

    @Autowired
    DiscoveryClient discoveryClient;


    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.zookeeper.connect-string", () -> getSocketAddressFor(ZOOKEEPER, 2181));
        registry.add("spring.kafka.bootstrap-servers", () -> getSocketAddressFor(KAFKA, 9092));
    }

    static String getSocketAddressFor(GenericContainer<?> container, int port) {
        return String.format("localhost:%d", container.getMappedPort(port));
    }


    @Test
    void contextLoads() {
        final var services = discoveryClient.getServices();
        Assertions.assertNotNull(services);
        Assertions.assertTrue(services.contains(applicationName));
        final var instances = discoveryClient.getInstances(applicationName);
        Assertions.assertNotNull(instances);
        Assertions.assertEquals(1, instances.size());
        final var instance = instances.get(0);
        log.info("""
                Instance values:
                  instanceId = {}
                  serviceId = {}
                  host = {}
                  port = {}
                  secure = {}
                  metadata = {}""",
            instance.getInstanceId(),
            instance.getServiceId(),
            instance.getHost(),
            instance.getPort(),
            instance.isSecure(),
            instance.getMetadata());
    }
}
