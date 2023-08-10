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

    // destination topic name
    static final String DESTINATION = "DEV.QUEUE.2";
    // verification timeout (ms)
    static final int TIMEOUT = 2000;
    // number of messages
    static final int N = 100;


    @SpyBean
    DualListenerService serviceToTest;


    @Test
    void test() {
        final var sentMsgSet = generateUuidSet(N, uuid -> jms.convertAndSend(DESTINATION, uuid));

        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenQueue_andAccept(ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));
    }
}
