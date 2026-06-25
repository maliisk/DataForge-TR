package com.dataforge.core.module.generator.service;

import com.dataforge.core.config.RabbitMqConfig;
import com.dataforge.core.payload.request.GenerateBatchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataGenerationListener {

    private final SyntheticDataService syntheticDataService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void handleBatchRequest(GenerateBatchRequest request) {
        log.info("RabbitMQ'dan {} adetlik {} senaryolu üretim görevi alındı. Başlıyor...",
                request.getCustomerCount(), request.getScenario());

        for (int i = 0; i < request.getCustomerCount(); i++) {
            syntheticDataService.generateCustomerByScenario(request.getScenario());
        }

        log.info("Toplu üretim görevi başarıyla tamamlandı!");
    }
}