package com.dataforge.core.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardMetricsResponse {
    private long totalCustomers;
    private long individualCount;
    private long corporateCount;
    private BigDecimal totalLiquidity;
    private BigDecimal totalDebt;
    private double averageRiskScore;
    private Map<String, Long> riskSegmentation;
}