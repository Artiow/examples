package artiow.examples.ibmmq;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class IbmMqDemoApplicationIntegrationTests extends AbstractIntegrationTests {

    @Test
    void test() {
        // destination queue name
        final var destination = "DEV.QUEUE.1";
        final var sentMsg = UUID.randomUUID().toString();
        jms.convertAndSend(destination, sentMsg);
        final var receivedMsg = jms.receiveAndConvert(destination);
        Assertions.assertNotSame(sentMsg, receivedMsg);
        Assertions.assertEquals(sentMsg, receivedMsg);
    }
}
