package artiow.examples.kafka.tracing;

import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.Assert;

@UtilityClass
public class TracingUtils {

    private static TracingService service;


    public static AutocloseableSpan withNextSpan(String name) {
        return service().withNextSpan(name);
    }

    public static <K, V> AutocloseableSpan withNextSpan(String name, ConsumerRecord<K, V> consumerRecord) {
        return service().withNextSpan(name, consumerRecord);
    }

    static void setService(TracingService service) {
        TracingUtils.service = service;
    }

    private static TracingService service() {
        Assert.state(service != null, () -> "No bean of type " + TracingService.class + " provided.");
        return service;
    }
}
