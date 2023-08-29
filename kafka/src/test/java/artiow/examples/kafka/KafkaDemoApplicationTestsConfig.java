package artiow.examples.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            // overriding Spring Boot configuration
            KafkaDemoApplication.class,
            // excluding specific beans
            KafkaConfig.class,
            ClockDemoProducer.class
        }))
public class KafkaDemoApplicationTestsConfig {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public <K, V> void configureKafkaTemplate(
        KafkaTemplate<K, V> kafkaTemplate,
        ConsumerFactory<K, V> consumerFactory) {
        kafkaTemplate.setConsumerFactory(consumerFactory);
    }
}
