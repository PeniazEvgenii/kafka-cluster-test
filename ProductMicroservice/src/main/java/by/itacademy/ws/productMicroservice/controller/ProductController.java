package by.itacademy.ws.productMicroservice.controller;

import by.itacademy.ws.productMicroservice.service.api.IProductService;
import by.itacademy.ws.productMicroservice.service.dto.CreateProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.message.CreateTopicsRequestData;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateProductDto createProductDto) {
        String productId = null;
        try {
            productId = productService.create(createProductDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("коза: {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(LocalDateTime.now(), e.getMessage()));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productId);
    }

}
