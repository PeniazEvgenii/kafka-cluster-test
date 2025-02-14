package by.itacademy.ws.productMicroservice.controller;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ErrorMessage {
    private final LocalDateTime localDateTime;
    private final String message;
}
