package artiow.examples.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;

@Configuration
@RequiredArgsConstructor
public class ProgrammaticContainersConfig implements KafkaListenerConfigurer {

    private final KafkaListenerService kafkaListenerService;


    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.registerEndpoint(ProgrammaticKafkaListenerEndpoint
            .builder()
            .id("programmaticEndpoint")
            .topics("kafka-topic-example")
            .messageListener(kafkaListenerService::consumeTick)
            .concurrency(4)
            .build());
    }
}
