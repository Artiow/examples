package artiow.examples.kafka;

import brave.Tracing;
import brave.kafka.clients.KafkaTracing;
import brave.messaging.MessagingRequest;
import brave.messaging.MessagingTracing;
import brave.sampler.SamplerFunction;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
public class BraveKafkaInstrumentationConfig {

    @Bean
    @ConditionalOnMissingBean
    public MessagingTracing messagingTracing(
        Tracing tracing,
        @Nullable SamplerFunction<MessagingRequest> producerSampler,
        @Nullable SamplerFunction<MessagingRequest> consumerSampler) {
        final var builder = MessagingTracing.newBuilder(tracing);
        Optional.ofNullable(producerSampler).ifPresent(builder::producerSampler);
        Optional.ofNullable(consumerSampler).ifPresent(builder::consumerSampler);
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaTracing kafkaTracing(MessagingTracing messagingTracing) {
        return KafkaTracing.create(messagingTracing);
    }

    @Bean
    public DefaultKafkaConsumerFactoryCustomizer defaultKafkaConsumerFactoryTraceCustomizer(
        KafkaTracing kafkaTracing) {
        return consumerFactory -> consumerFactory.addPostProcessor(kafkaTracing::consumer);
    }

    @Bean
    public DefaultKafkaProducerFactoryCustomizer defaultKafkaProducerFactoryTraceCustomizer(
        KafkaTracing kafkaTracing) {
        return producerFactory -> producerFactory.addPostProcessor(kafkaTracing::producer);
    }
}
