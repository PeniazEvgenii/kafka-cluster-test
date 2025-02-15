package by.itacademy.withdrawalService.handler;

import by.itacademy.library.event.WithdrawalRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "${name.withdraw-money-topic}",
containerFactory = "kafkaListener")
public class WithdrawalRequestedEventHandler {

    @KafkaHandler
    public void handle(@Payload WithdrawalRequestedEvent withdrawalRequestedEvent) {

        log.info("Received a new withdrawal event: {} ", withdrawalRequestedEvent.getAmount());
    }
}
