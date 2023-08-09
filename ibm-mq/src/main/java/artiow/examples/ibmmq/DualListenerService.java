package artiow.examples.ibmmq;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Slf4j(topic = "DEV.QUEUE.2")
@Service
public class DualListenerService {

    private static final String ACCEPTED = " ";
    private static final String REJECTED = "×";

    @JmsListener(
        destination = "DEV.QUEUE.2",
        containerFactory = "suppressedJmsListenerContainerFactory")
    public void listenQueue_andAccept(Message<UUID> msg) {
        log.info("[{}]: {}", ACCEPTED, msg.getPayload());
    }

    @JmsListener(
        destination = "DEV.QUEUE.2",
        containerFactory = "suppressedJmsListenerContainerFactory")
    public void listenQueue_andReject(Message<UUID> msg) {
        log.info("[{}]: {}", REJECTED, msg.getPayload());
        throw new RuntimeException();
    }
}
