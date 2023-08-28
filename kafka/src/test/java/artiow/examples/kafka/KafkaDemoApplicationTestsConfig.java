package artiow.examples.kafka;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            // overriding Spring Boot configuration
            KafkaDemoApplication.class,
            // excluding specific configs
            KafkaConfig.class
        }))
public class KafkaDemoApplicationTestsConfig {

}
