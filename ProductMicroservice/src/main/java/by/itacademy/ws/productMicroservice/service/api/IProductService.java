package by.itacademy.ws.productMicroservice.service.api;

import by.itacademy.ws.productMicroservice.service.dto.CreateProductDto;

import java.util.concurrent.ExecutionException;

public interface IProductService {

    String create(CreateProductDto createProductDto) throws ExecutionException, InterruptedException;
}
