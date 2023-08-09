package artiow.examples.ibmmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsListenerComponent {

    @JmsListener(
        destination = "DEV.BASE.TOPIC",
        containerFactory = "topicJmsListenerContainerFactory")
    public void listen(Message<String> msg) {
        log.info("{}: {}", "DEV.BASE.TOPIC", msg.getPayload());
    }
}
