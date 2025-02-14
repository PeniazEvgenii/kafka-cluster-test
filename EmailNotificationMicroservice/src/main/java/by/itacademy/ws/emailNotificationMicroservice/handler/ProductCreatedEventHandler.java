package by.itacademy.ws.emailNotificationMicroservice.handler;

import by.itacademy.library.event.ProductCreatedEvent;
import by.itacademy.ws.emailNotificationMicroservice.exception.NonRetryableException;
import by.itacademy.ws.emailNotificationMicroservice.exception.RetryableException;
import by.itacademy.ws.emailNotificationMicroservice.repository.entity.ProcessedEventEntity;
import by.itacademy.ws.emailNotificationMicroservice.repository.entity.ProcessedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(
        topics = "product-created-events-topic",
        containerFactory = "kafkaListener2")
public class ProductCreatedEventHandler {

    //    private static final Logger logg = LoggerFactory.getLogger(ProductCreatedEventHandler.class);
    private final RestTemplate restTemplate;
    private final ProcessedRepository processedRepository;

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {


        log.info("Receive event:{}", productCreatedEvent.getTitle());

        ProcessedEventEntity processedEventEntity = processedRepository.findByMessageId(messageId)
                .orElse(null);
        if (processedEventEntity != null) {
            log.info("ВСЕ OK уже такой messageId сохранен");
            return;
        }


        String url = "http://localhost:8090/response/200";
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Receive response:{}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            log.error(e.getMessage());

            log.error("RootCause: {}", e.getRootCause());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        ProcessedEventEntity processedEvent = ProcessedEventEntity.builder()
                .productId(messageKey)
                .messageId(messageId)
                .build();

        try{
            processedRepository.save(processedEvent);

        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        }

    }

//    @KafkaHandler
//    public void handle(ProductDeletedEvent productCreatedEvent) {
//
//    }
}
