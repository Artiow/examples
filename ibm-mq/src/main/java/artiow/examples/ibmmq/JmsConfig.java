package artiow.examples.ibmmq;

import jakarta.jms.ConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.util.ErrorHandler;

@EnableJms
@Configuration
public class JmsConfig {

    private static final ErrorHandler NO_OP_HANDLER = t -> { };


    @Bean
    public JmsListenerContainerFactory<?> topicJmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }

    @Bean
    public JmsListenerContainerFactory<?> suppressedJmsListenerContainerFactory(
        DefaultJmsListenerContainerFactoryConfigurer configurer,
        ConnectionFactory connectionFactory) {
        final var factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setErrorHandler(NO_OP_HANDLER);
        return factory;
    }
}
