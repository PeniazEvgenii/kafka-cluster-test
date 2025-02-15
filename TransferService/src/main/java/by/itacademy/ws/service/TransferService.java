package by.itacademy.ws.service;

import by.itacademy.library.event.DepositRequestedEvent;
import by.itacademy.library.event.WithdrawalRequestedEvent;
import by.itacademy.ws.error.TransferServiceException;
import by.itacademy.ws.model.TransferRestModel;
import by.itacademy.ws.repository.ITransferRepository;
import by.itacademy.ws.repository.TransferEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferService implements ITransferService {

    private final Environment environment;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ITransferRepository transferRepository;

    @Override
    @Transactional("transactionManager")
    public boolean transfer(TransferRestModel transferRestModel) {
        WithdrawalRequestedEvent withdrawalEvent = WithdrawalRequestedEvent.builder()
                .senderId(transferRestModel.getSenderId())
                .recepientId(transferRestModel.getRecepientId())
                .amount(transferRestModel.getAmount())
                .build();

        DepositRequestedEvent depositEvent = DepositRequestedEvent.builder()
                .senderId(transferRestModel.getSenderId())
                .recepientId(transferRestModel.getRecepientId())
                .amount(transferRestModel.getAmount())
                .build();

        try {
            TransferEntity transferEntity = TransferEntity.builder()
                    .transferId(UUID.randomUUID().toString())
                    .senderId(transferRestModel.getSenderId())
                    .recepientId(transferRestModel.getRecepientId())
                    .amount(transferRestModel.getAmount())
                    .build();
            transferRepository.saveAndFlush(transferEntity);

//            BeanUtils.copyProperties(transferEntity, transferRestModel); вместо этого маппер обычно


            kafkaTemplate.send(
                    "withdraw-money-topic",
                    withdrawalEvent);
            log.info("Sent event to withdrawal topic.");  // отправили в топик сколько денег снять

// Business logic that causes and error
            callRemoteServce();             // что-то производим на удаленном сервисе просто для демонстрации

            kafkaTemplate.send(
                    environment.getProperty("deposit-money-topic", "deposit-money-topic"),
                    depositEvent);
            log.info("Sent event to deposit topic");   // положить деньги на депозит сервис топик


        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new TransferServiceException(ex);
        }

        return true;
    }

    private ResponseEntity<String> callRemoteServce() throws Exception {
        String requestUrl = "http://localhost:8090/response/500";
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);

        if (response.getStatusCode().value() == HttpStatus.SERVICE_UNAVAILABLE.value()) {
            throw new Exception("Destination Microservice not availble");
        }

        if (response.getStatusCode().value() == HttpStatus.OK.value()) {
            log.info("Received response from mock service: " + response.getBody());
        }
        return response;
    }
}
