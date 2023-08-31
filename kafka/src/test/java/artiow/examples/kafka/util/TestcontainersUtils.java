package artiow.examples.kafka.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@UtilityClass
public class TestcontainersUtils {

    public static Supplier<Object> newSocketSupplier(ContainerState container, int originalPort) {
        final String socketAddress = container.getHost() + ":" + container.getMappedPort(originalPort);
        return () -> socketAddress;
    }

    public static Consumer<OutputFrame> slf4jLogConsumer(String loggerName) {
        return new Slf4jLogConsumer(LoggerFactory.getLogger(loggerName), true);
    }
}
