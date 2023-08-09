package artiow.examples.ibmmq;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class TopicListenerIntegrationTests extends AbstractIntegrationTests {

    @SpyBean
    TopicListenerService topicListenerService;


    @Test
    void test() {
        // destination topic name
        final var destination = "DEV.BASE.TOPIC";
        // verification timeout (ms)
        final var timeout = 1000;
        // number of messages
        final var n = 100;

        jms.setPubSubDomain(true); // topic
        final var sentMsgSet = MessageTestUtils.generate(n, uuid -> jms.convertAndSend(destination, uuid));

        Mockito
            .verify(topicListenerService, Mockito.timeout(timeout).times(n))
            .listenTopic_consumer1(ArgumentMatchers.argThat(msg -> MessageTestUtils.match(msg, sentMsgSet)));
        Mockito
            .verify(topicListenerService, Mockito.timeout(timeout).times(n))
            .listenTopic_consumer2(ArgumentMatchers.argThat(msg -> MessageTestUtils.match(msg, sentMsgSet)));
        Mockito
            .verify(topicListenerService, Mockito.timeout(timeout).times(n))
            .listenTopic_consumer3(ArgumentMatchers.argThat(msg -> MessageTestUtils.match(msg, sentMsgSet)));
    }
}
