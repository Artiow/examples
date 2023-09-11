package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import artiow.examples.kafka.utils.KafkaTestUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = KafkaDemoApplicationTestsConfig.class)
public class KafkaListenerTests extends AbstractKafkaTests {

    private static final Random RND = new Random();

    private static final String TOPIC = "kafka-topic-example";
    private static final int DATASET_LIMIT_ORIGIN = 75;
    private static final int DATASET_LIMIT_BOUND = 125;


    @Autowired
    KafkaTemplate<UUID, DemoData> kafkaTemplate;
    @SpyBean
    KafkaListenerService serviceToTest;


    private static int rndLimit() {
        return RND.nextInt(DATASET_LIMIT_ORIGIN, DATASET_LIMIT_BOUND);
    }


    @BeforeEach
    void beforeEach() {
        KafkaTestUtils.waitContainerForAssignment("annotationDrivenEndpoint", 4);
        KafkaTestUtils.waitContainerForAssignment("programmaticEndpoint", 4);
    }


    @Test
    @Order(0)
    void test_single() {
        // arrange
        final var testData = DemoData.generate();

        // act
        final var partition = new AtomicInteger(0);
        final var offset = new AtomicLong(0);
        kafkaTemplate
            .send(TOPIC, testData.getUuid(), testData)
            .thenAccept(sendResult -> {
                Assertions.assertNotNull(sendResult);
                final var producedRecord = sendResult.getProducerRecord();
                Assertions.assertNotNull(producedRecord);
                Assertions.assertEquals(TOPIC, producedRecord.topic());
                Assertions.assertSame(testData.getUuid(), producedRecord.key());
                Assertions.assertSame(testData, producedRecord.value());
                final var recordMetadata = sendResult.getRecordMetadata();
                Assertions.assertNotNull(recordMetadata);
                partition.set(recordMetadata.partition());
                offset.set(recordMetadata.offset());
            })
            .join();

        // assert
        Mockito
            .verify(serviceToTest, Mockito.timeout(1000).times(1))
            .consume(Mockito.argThat(new ConsumerRecordMatcher<>(partition.get(), Collections.singleton(testData))));
    }

    @Test
    void test_distinct() {
        IntStream
            // arrange
            .range(0, kafkaTemplate.partitionsFor(TOPIC).size())
            .mapToObj(partition -> Stream
                .generate(DemoData::generate)
                .map(data -> Maps.immutableEntry(partition, data))
                .limit(rndLimit()))
            .flatMap(Function.identity())
            // act
            .parallel()
            .peek(dataEntry -> {
                final var partition = dataEntry.getKey();
                final var data = dataEntry.getValue();
                kafkaTemplate.send(TOPIC, partition, data.getUuid(), data).join();
            })
            // assert
            .collect(Collectors.toUnmodifiableMap(
                Entry::getKey,
                dataEntry -> Collections.singleton(dataEntry.getValue()),
                Sets::union))
            .forEach((partition, dataSet) -> Mockito
                .verify(serviceToTest, Mockito.timeout(1000).times(dataSet.size()))
                .consume(Mockito.argThat(new ConsumerRecordMatcher<>(partition, dataSet))));
    }

    @Test
    void test_indistinct() {
        final var dataSet = Stream
            // arrange
            .generate(DemoData::generate)
            .limit(rndLimit())
            // act
            .parallel()
            .peek(data -> kafkaTemplate.send(TOPIC, data.getUuid(), data).join())
            .collect(Collectors.toSet());

        // assert
        Mockito
            .verify(serviceToTest, Mockito.timeout(1000).times(dataSet.size()))
            .consume(Mockito.argThat(new ConsumerRecordMatcher<>(dataSet)));
    }


    @RequiredArgsConstructor
    private static class ConsumerRecordMatcher<K, V> implements ArgumentMatcher<ConsumerRecord<K, V>> {

        private final Integer partition;
        private final Set<?> dataSet;


        public ConsumerRecordMatcher(Set<?> dataSet) {
            this(null, dataSet);
        }


        @Override
        public boolean matches(ConsumerRecord<K, V> argument) {
            return (partition == null || partition == argument.partition()) && dataSet.contains(argument.value());
        }

        @Override
        public Class<?> type() {
            return ConsumerRecord.class;
        }
    }
}
