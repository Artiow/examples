package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
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


    @Test
    void test() {
        IntStream
            // arrange
            .range(0, 8)
            .mapToObj(partition -> Stream
                .generate(DemoData::generate)
                .map(data -> Maps.immutableEntry(partition, data))
                .limit(RND.nextInt(75, 125)))
            .flatMap(Function.identity())
            // act
            .parallel()
            .peek(dataEntry -> {
                final var partition = dataEntry.getKey();
                final var data = dataEntry.getValue();
                kafkaTemplate.send("kafka-topic-example", partition, data.getUuid(), data).join();
            })
            // assert
            .collect(Collectors.toUnmodifiableMap(
                Entry::getKey,
                dataEntry -> Collections.singleton(dataEntry.getValue()),
                Sets::union))
            .forEach((partition, dataSet) -> Mockito
                .verify(serviceToTest, Mockito.timeout(1000).times(dataSet.size()))
                .consume(Mockito.argThat(rec -> rec.partition() == partition && dataSet.contains(rec.value()))));
    }
}
