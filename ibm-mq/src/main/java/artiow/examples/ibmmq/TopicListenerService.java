package artiow.examples.ibmmq;

import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessagingMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicListenerService implements JmsListenerConfigurer {

    private final MessageConverter messageConverter = new MessagingMessageConverter();
    private final JmsListenerContainerFactory<?> topicJmsListenerContainerFactory;


    // annotation-driven style
    @JmsListener(
        id = "annotation-driven-dev.base.topic-listener-1",
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listenTopic_annotationDrivenConsumer1(Message<UUID> msg) {
        log.info("[annotation-driven-consumer-1]: {}", msg.getPayload());
    }

    // annotation-driven style
    @JmsListener(
        id = "annotation-driven-dev.base.topic-listener-2",
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listenTopic_annotationDrivenConsumer2(Message<UUID> msg) {
        log.info("[annotation-driven-consumer-2]: {}", msg.getPayload());
    }

    // annotation-driven style
    @JmsListener(
        id = "annotation-driven-dev.base.topic-listener-3",
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listenTopic_annotationDrivenConsumer3(Message<UUID> msg) {
        log.info("[annotation-driven-consumer-3]: {}", msg.getPayload());
    }


    // programmatic style
    public void listenTopic_programmaticCommonConsumer(int consumerId, Message<UUID> msg) {
        log.info("[     programmatic-consumer-{}]: {}", consumerId, msg.getPayload());
    }


    @Override
    public void configureJmsListeners(@NonNull JmsListenerEndpointRegistrar registrar) {
        IntStream
            .range(1, 4)
            .mapToObj(id -> {
                final var endpoint = new SimpleJmsListenerEndpoint();
                endpoint.setId("programmatic-dev.base.topic-listener-" + id);
                endpoint.setDestination("DEV.BASE.TOPIC");
                endpoint.setMessageListener(msg -> listenTopic_programmaticCommonConsumer(id, adaptJmsMessage(msg, UUID.class)));
                return endpoint;
            })
            .forEach(endpoint -> {
                registrar.registerEndpoint(endpoint, topicJmsListenerContainerFactory);
            });
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <E> Message<E> adaptJmsMessage(jakarta.jms.Message msg, Class<E> payloadType) {
        return (Message<E>) messageConverter.fromMessage(msg);
    }
}
