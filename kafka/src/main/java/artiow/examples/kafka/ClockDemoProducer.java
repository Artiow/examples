package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import artiow.examples.kafka.tracing.TracingUtils;
import artiow.examples.kafka.utils.JavaTimeUtils;
import java.time.ZoneOffset;
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
        try (@SuppressWarnings("unused") final var span = TracingUtils.withNextSpan("produceTick")) {
            final var data = DemoData.generate();
            kafkaTemplate
                .send("kafka-topic-example", data.getUuid(), data)
                .thenAccept(sendResult -> {
                    final var record = sendResult.getProducerRecord();
                    final var meta = sendResult.getRecordMetadata();
                    log.info(
                        "[partition: {}; offset: {}; timestamp: {}] [key: {}; value: {}]",
                        meta.partition(),
                        meta.offset(),
                        JavaTimeUtils.localDateTime(meta.timestamp(), ZoneOffset.UTC),
                        record.key(),
                        record.value());
                });
        }
    }
}
