package by.itacademy.ws.productMicroservice;

import by.itacademy.library.event.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

@SpringBootTest
public class IdempotentProducerIT {

    @Autowired
    private KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @Test
    void testProducerConfig_whenIdempotenceEnable_assertsIdempotentProperties() {
        ProducerFactory<String, ProductCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();

        Map<String, Object> configurationProperties = producerFactory.getConfigurationProperties();

        Assertions.assertEquals(true, configurationProperties.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
        Assertions.assertEquals("all", configurationProperties.get(ProducerConfig.ACKS_CONFIG));

        if (configurationProperties.containsKey(ProducerConfig.RETRIES_CONFIG)) {
            Assertions.assertTrue(
                    Integer.parseInt(configurationProperties.get(ProducerConfig.RETRIES_CONFIG).toString()) > 0
            );
        }

        if (configurationProperties.containsKey(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION)) {
            Assertions.assertTrue(
                    Integer.parseInt(configurationProperties.get(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION).toString()) <= 5
            );
        }
    }
}
