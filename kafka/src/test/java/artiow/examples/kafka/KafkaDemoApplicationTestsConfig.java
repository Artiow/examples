package artiow.examples.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

@Configuration
@EnableAutoConfiguration
@ActiveProfiles("test")
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            // overriding Spring Boot configuration
            KafkaDemoApplication.class,
            // excluding specific beans
            ClockDemoProducer.class
        }))
public class KafkaDemoApplicationTestsConfig {

    private static KafkaListenerEndpointRegistry endpointRegistry;


    public static KafkaListenerEndpointRegistry getEndpointRegistry() {
        Assert.state(
            endpointRegistry != null,
            () -> "No bean of type " + KafkaListenerEndpointRegistry.class + " provided. "
                + "Maybe " + KafkaDemoApplicationTestsConfig.class + " isn't loaded in application context?");
        return endpointRegistry;
    }



    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public <K, V> void configureKafkaTemplate(
        KafkaTemplate<K, V> kafkaTemplate,
        ConsumerFactory<K, V> consumerFactory) {
        kafkaTemplate.setConsumerFactory(consumerFactory);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void setKafkaListenerEndpointRegistry(
        KafkaListenerEndpointRegistry endpointRegistry) {
        KafkaDemoApplicationTestsConfig.endpointRegistry = endpointRegistry;
    }
}
