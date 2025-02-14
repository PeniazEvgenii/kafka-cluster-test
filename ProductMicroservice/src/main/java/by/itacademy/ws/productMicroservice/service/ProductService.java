package by.itacademy.ws.productMicroservice.service;

import by.itacademy.library.event.ProductCreatedEvent;
import by.itacademy.ws.productMicroservice.service.api.IProductService;
import by.itacademy.ws.productMicroservice.service.dto.CreateProductDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate2;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    @Override
    public String create(CreateProductDto createProductDto) throws ExecutionException, InterruptedException {
        //TODO save to database
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = ProductCreatedEvent.builder()
                .productId(productId)
                .title(createProductDto.getTitle())
                .price(createProductDto.getPrice())
                .quantity(createProductDto.getQuantity())
                .build();

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                "product-created-events-topic",
                productId,
                productCreatedEvent
        );
        record.headers().add("messageId", "UUID.randomUUID().toString()".getBytes());


//        SendResult<String, ProductCreatedEvent> result = kafkaTemplate2
//                .send("product-created-events-topic", productId, productCreatedEvent).get();

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate2
                .send(record).get();

        logger.info("Topic: {}", result.getRecordMetadata().topic());
        logger.info("Partition: {}", result.getRecordMetadata().partition());
        logger.info("Offset: {}", result.getRecordMetadata().offset());

        logger.info("return productId: {}", productId);

        return productId;
    }
}
