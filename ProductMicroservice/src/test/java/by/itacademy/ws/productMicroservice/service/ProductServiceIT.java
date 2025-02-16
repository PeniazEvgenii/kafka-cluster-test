package by.itacademy.ws.productMicroservice.service;

import by.itacademy.library.event.ProductCreatedEvent;
import by.itacademy.ws.productMicroservice.service.api.IProductService;
import by.itacademy.ws.productMicroservice.service.dto.CreateProductDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.convention.TestBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@EmbeddedKafka
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}")
class ProductServiceIT {

    @Autowired
    private IProductService productService;
    @Autowired
    private Environment environment;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private KafkaMessageListenerContainer<String, ProductCreatedEvent> container;
    private BlockingQueue<ConsumerRecord<String, ProductCreatedEvent>> records;


    /**
     * Метод setupMessageListener устанавливает обработчик, который вызывается каждый раз,
     * когда контейнер получает новое сообщение из Kafka. В нашем случае,
     * этот обработчик просто добавляет полученное сообщение в очередь records.
     * Зачем он нужен:
     * Благодаря этому, тест может асинхронно ожидать получения сообщения.
     * После вызова productService.create(createProductDto), сервис публикует сообщение в Kafka,
     * а наш консьюмер (контейнер) ловит это сообщение, и мы можем проверить его содержимое в тестовом методе.
     * Это позволяет проверить корректность работы механизма публикации сообщений.
     */
    @BeforeAll
    void setUp() {

        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(getConsumerProperties());

        ContainerProperties containerProperties = new ContainerProperties(environment.getProperty("product-created-events-topic-name"));

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingDeque<>();
        container.setupMessageListener((MessageListener<String, ProductCreatedEvent>)records::add);

        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }


    @Test
    void TestCreateProduct_whenGivenValidProductDetails_successfullySendKafkaMessage() throws ExecutionException, InterruptedException {
        CreateProductDto createProductDto = CreateProductDto.builder()
                .title("samsung")
                .price(new BigDecimal(100))
                .quantity(new BigDecimal(1))
                .build();

        productService.create(createProductDto);

        ConsumerRecord<String, ProductCreatedEvent> message = records.poll(3000, TimeUnit.MILLISECONDS);
        assertNotNull(message);
        assertNotNull(message.key());
        ProductCreatedEvent productCreatedEvent = message.value();
        assertAll(
                () -> assertEquals(createProductDto.getTitle(), productCreatedEvent.getTitle()),
                () -> assertEquals(createProductDto.getQuantity(), productCreatedEvent.getQuantity()),
                () -> assertEquals(createProductDto.getPrice(), productCreatedEvent.getPrice())
        );

    }


    private Map<String, Object> getConsumerProperties() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, environment.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty("spring.kafka.consumer.auto-offset-reset"));

        return config;
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }
}