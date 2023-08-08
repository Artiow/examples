package artiow.examples.ibmmq;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class IbmMqApplicationIntegrationTests {

    @Container
    @SuppressWarnings("resource")
    static final GenericContainer<?> CONTAINER =
        new GenericContainer<>("icr.io/ibm-messaging/mq")
            .withEnv("LICENSE", "accept")
            .withEnv("MQ_QMGR_NAME", "QM1")
            .withExposedPorts(1414)
            .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("icr.io/ibm-messaging/mq"), true))
            .waitingFor(Wait.forLogMessage(".* Started queue manager\\n", 1));


    @Autowired
    JmsTemplate jms;


    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("ibm.mq.conn-name", IbmMqApplicationIntegrationTests::getConnectionName);
    }

    static String getConnectionName() {
        return String.format("localhost(%d)", CONTAINER.getMappedPort(1414));
    }


    @Test
    void test() {
        final var sentMsg = UUID.randomUUID().toString();
        jms.convertAndSend("DEV.QUEUE.1", sentMsg);
        final var receivedMsg = jms.receiveAndConvert("DEV.QUEUE.1");
        Assertions.assertNotSame(sentMsg, receivedMsg);
        Assertions.assertEquals(sentMsg, receivedMsg);
    }
}
