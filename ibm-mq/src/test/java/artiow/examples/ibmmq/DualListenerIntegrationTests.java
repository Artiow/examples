package artiow.examples.ibmmq;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class DualListenerIntegrationTests extends AbstractIntegrationTests {

    @SpyBean
    DualListenerService dualListenerService;

    @Test
    void test() {
        // destination queue name
        final var destination = "DEV.QUEUE.2";
        // verification timeout (ms)
        final var timeout = 1000;
        // number of messages
        final var n = 100;

        final var sentMsgSet = MessageTestUtils.generate(n, uuid -> jms.convertAndSend(destination, uuid));

        Mockito
            .verify(dualListenerService, Mockito.timeout(timeout).times(n))
            .listenQueue_andAccept(ArgumentMatchers.argThat(msg -> MessageTestUtils.match(msg, sentMsgSet)));
    }
}
