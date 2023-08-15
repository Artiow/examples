package artiow.example.kafka;

import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.Slf4jLogConsumer;

@UtilityClass
public class TestcontainersUtils {

    public static Consumer<OutputFrame> slf4jLogConsumer(String loggerName) {
        return new Slf4jLogConsumer(LoggerFactory.getLogger(loggerName), true);
    }
}
