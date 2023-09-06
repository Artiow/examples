package artiow.examples.kafka;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.TopicPartitionOffset;

@Configuration
@RequiredArgsConstructor
public class ProgrammaticContainersConfig {

    private final ConcurrentKafkaListenerContainerFactory<?, ?> containerFactory;


    @Autowired
    public void configureKafkaListenerService(KafkaListenerService kafkaListenerService) {
        containerBuilder()
            .topic("kafka-topic-example")
            .partitions(1, 3, 5, 7)
            .listener(kafkaListenerService::consume)
            .name("programmaticContainer")
            .concurrency(4)
            .buildAndStart();
    }


    private <K, V> ContainerBuilder<K, V> containerBuilder() {
        return new ContainerBuilder<>();
    }


    @Setter
    @Accessors(chain = true, fluent = true)
    private class ContainerBuilder<K, V> {

        private String topic;
        private int[] partitions;
        private MessageListener<K, V> listener;
        private String name;
        private int concurrency;


        private static TopicPartitionOffset[] topicPartitions(String topic, int... partitions) {
            return IntStream
                .of(partitions)
                .mapToObj(partition -> new TopicPartitionOffset(topic, partition))
                .toArray(TopicPartitionOffset[]::new);
        }


        public ContainerBuilder<K, V> partitions(int... partitions) {
            this.partitions = partitions;
            return this;
        }


        public void buildAndStart() {
            build().start();
        }

        public ConcurrentMessageListenerContainer<K, V> build() {
            final var container = ArrayUtils.isNotEmpty(partitions)
                ? factory().createContainer(topicPartitions(topic, partitions))
                : factory().createContainer(topic);
            container.setupMessageListener(listener);
            container.setBeanName(name);
            container.setConcurrency(concurrency);
            return container;
        }

        private ConcurrentKafkaListenerContainerFactory<K, V> factory() {
            // noinspection unchecked
            return (ConcurrentKafkaListenerContainerFactory<K, V>) ProgrammaticContainersConfig.this.containerFactory;
        }
    }
}
