package artiow.examples.ibmmq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class TopicListenerIntegrationTests extends AbstractIntegrationTests {

    // destination topic name
    static final String DESTINATION = "DEV.BASE.TOPIC";
    // verification timeout (ms)
    static final int TIMEOUT = 2000;
    // number of messages
    static final int N = 100;

    @SpyBean
    TopicListenerService serviceToTest;


    @BeforeEach
    void beforeEach() {
        jms.setPubSubDomain(true); // topic
    }


    @Test
    void test() {
        final var sentMsgSet = generateUuidSet(N, uuid -> jms.convertAndSend(DESTINATION, uuid));

        // annotation-driven style test
        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenTopic_annotationDrivenConsumer1(
                ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));
        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenTopic_annotationDrivenConsumer2(
                ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));
        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenTopic_annotationDrivenConsumer3(
                ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));

        // programmatic style test
        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenTopic_programmaticCommonConsumer(
                ArgumentMatchers.eq(1),
                ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));
        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenTopic_programmaticCommonConsumer(
                ArgumentMatchers.eq(2),
                ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));
        Mockito
            .verify(serviceToTest, Mockito.timeout(TIMEOUT).times(N))
            .listenTopic_programmaticCommonConsumer(
                ArgumentMatchers.eq(3),
                ArgumentMatchers.argThat(msg -> isUuidInSet(msg, sentMsgSet)));
    }
}
