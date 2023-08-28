package artiow.examples.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemoProducer {

    private final KafkaTemplate<Object, Object> kafkaTemplate;


    @Scheduled(cron = "*/1 * * * * *")
    public void send() {
        final var data = DemoData.generate();
        kafkaTemplate.send("kafka-topic-example", data.getUuid(), data);
    }
}
