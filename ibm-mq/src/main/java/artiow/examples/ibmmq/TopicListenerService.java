package artiow.examples.ibmmq;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j(topic = "DEV.BASE.TOPIC")
@Service
public class TopicListenerService {

    @JmsListener(
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listenTopic_consumer1(Message<UUID> msg) {
        log.info("[consumer1]: {}", msg.getPayload());
    }

    @JmsListener(
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listenTopic_consumer2(Message<UUID> msg) {
        log.info("[consumer2]: {}", msg.getPayload());
    }

    @JmsListener(
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listenTopic_consumer3(Message<UUID> msg) {
        log.info("[consumer3]: {}", msg.getPayload());
    }
}
