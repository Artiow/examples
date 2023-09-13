package artiow.examples.kafka;

import artiow.examples.kafka.tracing.TracingUtils;
import artiow.examples.kafka.utils.JavaTimeUtils;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaListenerService {

    @KafkaListener(id = "annotationDrivenEndpoint", idIsGroup = false, topics = "kafka-topic-example", concurrency = "4")
    public void consumeTick(ConsumerRecord<Object, Object> record) {
        try (@SuppressWarnings("unused") final var span = TracingUtils.withNextSpan("consumeTick", record)) {
            log.info(
                "[partition: {}; offset: {}; timestamp: {}] [key: {}; value: {}]",
                record.partition(),
                record.offset(),
                JavaTimeUtils.localDateTime(record.timestamp(), ZoneOffset.UTC),
                record.key(),
                record.value());
        }
    }
}
