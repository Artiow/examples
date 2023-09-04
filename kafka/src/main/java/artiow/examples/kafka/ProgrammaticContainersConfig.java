package artiow.examples.kafka;

import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
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


    private static TopicPartitionOffset[] topicPartitions(String topic, int... partitions) {
        return IntStream
            .of(partitions)
            .mapToObj(partition -> new TopicPartitionOffset(topic, partition))
            .toArray(TopicPartitionOffset[]::new);
    }


    @Autowired
    public void configureKafkaListenerService(KafkaListenerService kafkaListenerService) {
        listen("kafka-topic-example", 1, 3, 5, 7)
            .listener(kafkaListenerService::listen_programmaticConsumer)
            .name("programmaticContainer")
            .concurrency(4)
            .start();
    }


    private <K, V> ContainerStarter<K, V> listen(String topic, int... partitions) {
        return new ContainerStarter<>(topicPartitions(topic, partitions));
    }


    private class ContainerStarter<K, V> {

        private final ConcurrentMessageListenerContainer<K, V> container;


        public ContainerStarter(TopicPartitionOffset[] topicPartitionOffsets) {
            // noinspection unchecked
            this.container = (ConcurrentMessageListenerContainer<K, V>) ProgrammaticContainersConfig.this.containerFactory.createContainer(topicPartitionOffsets);
        }


        public ContainerStarter<K, V> listener(MessageListener<K, V> listener) {
            this.container.setupMessageListener(listener);
            return this;
        }

        public ContainerStarter<K, V> name(String name) {
            this.container.setBeanName(name);
            return this;
        }

        public ContainerStarter<K, V> concurrency(int concurrency) {
            this.container.setConcurrency(concurrency);
            return this;
        }

        public void start() {
            this.container.start();
        }
    }
}
