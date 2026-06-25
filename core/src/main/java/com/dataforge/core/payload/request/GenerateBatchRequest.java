package com.dataforge.core.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class GenerateBatchRequest {

    @Min(value = 1, message = "En az 1 adet müşteri üretilmelidir.")
    @Max(value = 10000, message = "Tek seferde en fazla 10.000 müşteri üretilebilir.")
    private int customerCount;

    private ScenarioType scenario = ScenarioType.NORMAL_CUSTOMER;
}