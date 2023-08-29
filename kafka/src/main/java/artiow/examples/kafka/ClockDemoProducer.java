package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClockDemoProducer {

    private final KafkaTemplate<UUID, DemoData> kafkaTemplate;
    private final Set<UUID> producedUuids = new HashSet<>();


    @Scheduled(cron = "*/1 * * * * *")
    public void produceTick() {
        final var data = DemoData.generate();
        kafkaTemplate
            .send("kafka-topic-example", data.getUuid(), data)
            .thenAccept(sendResult -> producedUuids.add(sendResult.getProducerRecord().key()));
    }

    @KafkaListener(groupId = "kafka-demo-group", topics = {"kafka-topic-example"})
    public void consumeTick(ConsumerRecord<UUID, DemoData> rec) {
        final var uuid = Objects.requireNonNull(rec.key(), "Key must be non null");
        final var data = Objects.requireNonNull(rec.value(), "Value must be non null");

        if (!Objects.equals(uuid, data.getUuid())) {
            throw new IllegalArgumentException(String.format(
                "Consumed record is not consistent: key = %s, value.uuid = %s",
                uuid,
                data.getUuid()));
        }

        final var offset = rec.offset();
        final var timestamp = LocalDateTime.ofEpochSecond(
            rec.timestamp() / 1000,
            ((int) (rec.timestamp() % 1000)) * 1_000_000,
            ZoneOffset.UTC);
        if (!producedUuids.contains(uuid)) {
            log.warn("""
                Unknown key: {} (offset: {}, timestamp: {}, value: {})""",
                uuid,
                offset,
                timestamp,
                data);
        } else {
            log.info("""
                Consumed key: {} (offset: {}, timestamp: {}, value: {})""",
                uuid,
                offset,
                timestamp,
                data);
        }
    }
}
