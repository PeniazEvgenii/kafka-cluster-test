package by.itacademy.depositService.handler;

import by.itacademy.library.event.DepositRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "${name.deposit-money-topic}", containerFactory = "kafkaListener3")
public class DepositRequestedEventHandler {

    @KafkaHandler
    public void handle(@Payload DepositRequestedEvent depositRequestedEvent) {

        log.info("Received a new deposit event: {} ", depositRequestedEvent.getAmount());
    }
}
