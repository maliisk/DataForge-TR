package com.dataforge.core.module.analytics.controller;

import com.dataforge.core.payload.response.ApiResponse;
import com.dataforge.core.payload.response.DashboardMetricsResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final EntityManager entityManager;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardMetricsResponse>> getDashboardMetrics() {
        Long totalCustomers = (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c").getSingleResult();
        Long individualCount = (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.customerType = 'INDIVIDUAL'").getSingleResult();
        Long corporateCount = (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.customerType = 'CORPORATE'").getSingleResult();

        Double avgRiskScore = (Double) entityManager.createQuery("SELECT AVG(c.riskScore) FROM Customer c").getSingleResult();

        BigDecimal totalLiquidity = (BigDecimal) entityManager.createQuery("SELECT COALESCE(SUM(a.balance), 0) FROM Account a").getSingleResult();
        BigDecimal totalLoanDebt = (BigDecimal) entityManager.createQuery("SELECT COALESCE(SUM(l.remainingDebt), 0) FROM Loan l").getSingleResult();
        BigDecimal totalCardDebt = (BigDecimal) entityManager.createQuery("SELECT COALESCE(SUM(cc.currentDebt), 0) FROM CreditCard cc").getSingleResult();
        BigDecimal totalDebt = totalLoanDebt.add(totalCardDebt);

        Map<String, Long> riskSegments = new LinkedHashMap<>();
        riskSegments.put("Güvenli (0-30)", (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.riskScore <= 30").getSingleResult());
        riskSegments.put("Dikkatli (31-60)", (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.riskScore BETWEEN 31 AND 60").getSingleResult());
        riskSegments.put("Yüksek Risk (61-84)", (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.riskScore BETWEEN 61 AND 84").getSingleResult());
        riskSegments.put("Kritik/Fraud (85-100)", (Long) entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.riskScore >= 85").getSingleResult());

        DashboardMetricsResponse metrics = DashboardMetricsResponse.builder()
                .totalCustomers(totalCustomers)
                .individualCount(individualCount)
                .corporateCount(corporateCount)
                .totalLiquidity(totalLiquidity)
                .totalDebt(totalDebt)
                .averageRiskScore(avgRiskScore != null ? Math.round(avgRiskScore * 100.0) / 100.0 : 0.0)
                .riskSegmentation(riskSegments)
                .build();

        return ResponseEntity.ok(ApiResponse.<DashboardMetricsResponse>builder()
                .success(true)
                .message("Analitik metrikler başarıyla hesaplandı.")
                .data(metrics)
                .timestamp(LocalDateTime.now())
                .build());
    }
}