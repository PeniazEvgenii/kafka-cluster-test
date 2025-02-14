package by.itacademy.ws.productMicroservice.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
public class CreateProductDto {

    private String title;
    private BigDecimal price;
    @JsonProperty(value = "quan_tity")
    private BigDecimal quantity;
}
