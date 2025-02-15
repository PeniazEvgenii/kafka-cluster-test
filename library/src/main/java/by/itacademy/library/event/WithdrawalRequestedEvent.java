package by.itacademy.library.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawalRequestedEvent {

    private String senderId;
    private String recepientId;
    private BigDecimal amount;
}
