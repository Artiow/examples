package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClockDemoProducer {

    private final KafkaTemplate<UUID, DemoData> kafkaTemplate;


    @Scheduled(cron = "*/1 * * * * *")
    public void produceTick() {
        final var data = DemoData.generate();
        kafkaTemplate.send("kafka-topic-example", data.getUuid(), data);
    }
}
