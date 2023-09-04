package artiow.examples.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic() {
        return TopicBuilder
            .name("kafka-topic-example")
            .partitions(8)
            .build();
    }

    @Bean
    public CommonErrorHandler errorHandler() {
        final var log = LoggerFactory.getLogger(getClass() + ".errorHandler");
        return new DefaultErrorHandler((record, exception) -> {
            if (exception instanceof ListenerExecutionFailedException listenerException) {
                final var originException = listenerException.getCause();
                log.error(
                    "Record {} skipped because of an exception {} caused by {}",
                    record.toString(),
                    listenerException.toString(),
                    originException.toString());
            } else {
                log.error(
                    "Record {} skipped because of an exception {}",
                    record.toString(),
                    exception.toString());
            }
        }, new FixedBackOff(0, 0));
    }
}
