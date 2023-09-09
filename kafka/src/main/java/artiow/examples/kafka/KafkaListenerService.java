package artiow.examples.kafka;

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
    public void consume(ConsumerRecord<Object, Object> rec) {
        log.info(
            "[partition: {}; offset: {}; timestamp: {}] [key: {}; value: {}]",
            rec.partition(),
            rec.offset(),
            JavaTimeUtils.localDateTime(rec.timestamp(), ZoneOffset.UTC),
            rec.key(),
            rec.value());
    }
}