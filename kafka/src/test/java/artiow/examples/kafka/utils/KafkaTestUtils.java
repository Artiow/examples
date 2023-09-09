package artiow.examples.kafka.utils;

import artiow.examples.kafka.KafkaDemoApplicationTestsConfig;
import lombok.experimental.UtilityClass;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.util.Assert;

@UtilityClass
public class KafkaTestUtils {

    public static KafkaListenerEndpointRegistry getEndpointRegistry() {
        return KafkaDemoApplicationTestsConfig.getEndpointRegistry();
    }

    public static MessageListenerContainer getMessageListenerContainer(String id) {
        return getEndpointRegistry().getListenerContainer(id);
    }

    public static void waitContainerForAssignment(String containerId, int partitions) {
        final var container = getMessageListenerContainer(containerId);
        Assert.state(container != null, () -> "No listener container with id " + containerId + " provided");
        ContainerTestUtils.waitForAssignment(container, partitions);
    }
}
