package by.itacademy.library.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DepositRequestedEvent {

    private String senderId;
    private String recepientId;
    private BigDecimal amount;
}
