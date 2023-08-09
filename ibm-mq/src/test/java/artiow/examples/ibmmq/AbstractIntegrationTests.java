package artiow.examples.ibmmq;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

abstract class AbstractIntegrationTests {

    @Container
    @SuppressWarnings("resource")
    private static final GenericContainer<?> CONTAINER =
        new GenericContainer<>("icr.io/ibm-messaging/mq")
            .withEnv("LICENSE", "accept")
            .withEnv("MQ_QMGR_NAME", "QM1")
            .withExposedPorts(1414)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("icr.io/ibm-messaging/mq"), true))
            .waitingFor(Wait.forLogMessage(".* Started queue manager\\n", 1));


    @Autowired
    JmsTemplate jms;


    @DynamicPropertySource
    private static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("ibm.mq.conn-name", AbstractIntegrationTests::getConnectionName);
    }

    private static String getConnectionName() {
        return String.format("localhost(%d)", CONTAINER.getMappedPort(1414));
    }
}
