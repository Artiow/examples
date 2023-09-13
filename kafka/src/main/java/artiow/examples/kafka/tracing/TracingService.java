package artiow.examples.kafka.tracing;

import brave.kafka.clients.KafkaTracing;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveSpan;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
public class TracingService {

    private final Tracer tracer;
    private final KafkaTracing kafkaTracing;


    public TracingService(Tracer tracer, KafkaTracing kafkaTracing) {
        this.tracer = tracer;
        this.kafkaTracing = kafkaTracing;
        TracingUtils.setService(this);
    }


    public AutocloseableSpan withNextSpan(String name) {
        final var span = tracer.nextSpan().name(name).start();
        final var ws = tracer.withSpan(span);
        return new AutocloseableSpan(span, ws);
    }

    public <K, V> AutocloseableSpan withNextSpan(String name, ConsumerRecord<K, V> consumerRecord) {
        final var span = BraveSpan.fromBrave(kafkaTracing.nextSpan(consumerRecord)).name(name).start();
        final var ws = tracer.withSpan(span);
        return new AutocloseableSpan(span, ws);
    }
}
