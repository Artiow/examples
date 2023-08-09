package artiow.examples.ibmmq;

import com.ibm.mq.jakarta.jms.MQTopic;
import jakarta.jms.JMSException;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

@Deprecated
@SpringBootTest
class IbmMqApplicationLocalTests {

    @Autowired
    JmsTemplate jms;

    @Test
    void test0() {
        final var sentMsg = UUID.randomUUID().toString();
        jms.convertAndSend("DEV.QUEUE.1", sentMsg);
        final var receivedMsg = jms.receiveAndConvert("DEV.QUEUE.1");
        Assertions.assertNotSame(sentMsg, receivedMsg);
        Assertions.assertEquals(sentMsg, receivedMsg);
    }

    @Test
    void test1() throws JMSException {
        final var sentMsg = UUID.randomUUID().toString();
        jms.convertAndSend(new MQTopic("DEV.BASE.TOPIC"), sentMsg);
    }
}
