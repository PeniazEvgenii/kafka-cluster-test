package by.itacademy.ws.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class TransferRestModel {

    private final String senderId;

    private final String recepientId;

    private final BigDecimal amount;
}
