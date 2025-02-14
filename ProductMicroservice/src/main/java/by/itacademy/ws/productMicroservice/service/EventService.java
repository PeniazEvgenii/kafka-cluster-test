package by.itacademy.ws.productMicroservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    public void sendEvent(String key, T event, String topic) {

        CompletableFuture<SendResult<String, T>> send = kafkaTemplate.send(topic, key, event);

        send.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("error");
            } else {
                log.info("результат: {}", result);
            }
        });
    }

}
