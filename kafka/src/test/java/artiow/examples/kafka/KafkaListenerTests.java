package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@SpringBootTest(classes = KafkaDemoApplicationTestsConfig.class)
public class KafkaListenerTests extends AbstractKafkaTests {

    private static final Random RND = new Random();

    @Autowired
    KafkaTemplate<UUID, DemoData> kafkaTemplate;
    @SpyBean
    KafkaListenerService serviceToTest;


    private static Set<DemoData> generateDataSet(int limit, Function<DemoData, CompletableFuture<?>> action) {
        return Stream
            .generate(DemoData::generate)
            .limit(limit)
            .parallel()
            .peek(demoData -> action.apply(demoData).join())
            .collect(Collectors.toSet());
    }


    @Test
    void test() {
        final var dataSet = IntStream
            .range(0, 8)
            .mapToObj(partition -> generateDataSet(
                RND.nextInt(10, 20),
                data -> kafkaTemplate.send("kafka-topic-example", partition, data.getUuid(), data)))
            .toArray(n -> (Set<DemoData>[]) new Set[n]);

        IntStream
            .range(0, 8)
            .forEach(partition -> Mockito
                .verify(serviceToTest, Mockito.timeout(5000).times(dataSet[partition].size()))
                .consume(Mockito.argThat(rec -> rec.partition() == partition && dataSet[partition].contains(rec.value()))));
    }
}
