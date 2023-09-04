package artiow.examples.kafka;

import artiow.examples.kafka.utils.JavaTimeUtils;
import jakarta.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final ConcurrentKafkaListenerContainerFactory<?, ?> containerFactory;


    private static TopicPartitionOffset[] topicPartitions(String topic, int... partitions) {
        return IntStream
            .of(partitions)
            .mapToObj(partition -> new TopicPartitionOffset(topic, partition))
            .toArray(TopicPartitionOffset[]::new);
    }


    @PostConstruct
    private void init() {
        final var container = containerFactory.createContainer(topicPartitions("kafka-topic-example", 1, 3, 5, 7));
        container.setupMessageListener((MessageListener<Object, Object>) this::listen_programmaticConsumer);
        container.setBeanName("programmaticContainer");
        container.setConcurrency(4);
        container.start();
    }


    @KafkaListener(
        topicPartitions = {@TopicPartition(topic = "kafka-topic-example", partitions = "0, 2, 4, 6")},
        concurrency = "4")
    public void listen_annotationDrivenConsumer(ConsumerRecord<Object, Object> rec) {
        consume(rec);
    }

    public void listen_programmaticConsumer(ConsumerRecord<Object, Object> rec) {
        consume(rec);
    }


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
