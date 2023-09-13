package artiow.examples.kafka;

import artiow.examples.kafka.dto.DemoData;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@AutoConfigureObservability
@SpringBootTest(classes = KafkaDemoApplicationTestsConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class KafkaDemoApplicationTests extends AbstractKafkaTests {

    @Value("${spring.application.name}")
    String applicationName;
    @Autowired
    DiscoveryClient discoveryClient;
    @Autowired
    KafkaTemplate<UUID, DemoData> kafkaTemplate;


    @Test
    void testServiceDiscovery() {
        final var services = discoveryClient.getServices();
        Assertions.assertNotNull(services);
        Assertions.assertTrue(services.contains(applicationName));

        final var instances = discoveryClient.getInstances(applicationName);
        Assertions.assertNotNull(instances);
        Assertions.assertEquals(1, instances.size());

        final var instance = instances.get(0);
        log.info("""
                Instance values:
                  instanceId = {}
                  serviceId = {}
                  host = {}
                  port = {}
                  secure = {}
                  metadata = {}""",
            instance.getInstanceId(),
            instance.getServiceId(),
            instance.getHost(),
            instance.getPort(),
            instance.isSecure(),
            instance.getMetadata());
    }

    @Test
    void testKafka() {
        final var topic = UUID.randomUUID().toString();
        final var testData = DemoData.generate();

        kafkaTemplate
            .send(topic, testData.getUuid(), testData)
            .thenAccept(sendResult -> {
                final var producedRecord = sendResult.getProducerRecord();
                Assertions.assertNotNull(producedRecord);
                Assertions.assertEquals(topic, producedRecord.topic());
                Assertions.assertSame(testData.getUuid(), producedRecord.key());
                Assertions.assertSame(testData, producedRecord.value());
            })
            .join();

        final var consumedRecord = kafkaTemplate.receive(topic, 0, 0);
        Assertions.assertNotNull(consumedRecord);
        Assertions.assertEquals(topic, consumedRecord.topic());
        Assertions.assertNotSame(testData.getUuid(), consumedRecord.key());
        Assertions.assertEquals(testData.getUuid(), consumedRecord.key());
        Assertions.assertNotSame(testData, consumedRecord.value());
        Assertions.assertEquals(testData, consumedRecord.value());
    }
}
