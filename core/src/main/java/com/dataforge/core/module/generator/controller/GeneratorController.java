package com.dataforge.core.module.generator.controller;

import com.dataforge.core.module.customer.entity.Customer;
import com.dataforge.core.module.generator.service.SyntheticDataService;
import com.dataforge.core.payload.request.GenerateBatchRequest;
import com.dataforge.core.payload.request.ScenarioType;
import com.dataforge.core.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/generator")
@RequiredArgsConstructor
public class GeneratorController {

    private final SyntheticDataService syntheticDataService;

    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<Customer>> generateCustomer() {

        Customer generatedCustomer = syntheticDataService.generateCustomerByScenario(ScenarioType.NORMAL_CUSTOMER);

        ApiResponse<Customer> response = ApiResponse.<Customer>builder()
                .success(true)
                .message("Sentetik müşteri ve hesabı başarıyla üretildi.")
                .data(generatedCustomer)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> generateBatch(@RequestBody GenerateBatchRequest request) {
        syntheticDataService.publishBatchRequest(request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message(request.getCustomerCount() + " adet müşteri (" + request.getScenario() + ") üretim talebi sıraya alındı. Arka planda işleniyor.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}