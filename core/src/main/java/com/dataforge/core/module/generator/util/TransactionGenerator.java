package com.dataforge.core.module.generator.util;

import com.dataforge.core.module.account.entity.Account;
import com.dataforge.core.module.transaction.entity.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class TransactionGenerator {

    private TransactionGenerator() {}


    public static List<Transaction> generateTransactionsForAccount(Account account, int numberOfTransactions) {
        List<Transaction> transactions = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        LocalDateTime currentTxDate = LocalDateTime.now().minusDays(30);

        double baseSalary = account.getBalance().doubleValue() * random.nextDouble(0.8, 1.5);

        for (int i = 0; i < numberOfTransactions; i++) {
            currentTxDate = currentTxDate.plusHours(random.nextInt(1, 25));

            String type;
            double amountRaw;

            int probability = random.nextInt(100);
            if (probability < 5) {
                type = "MAAS_YATMASI";
                amountRaw = baseSalary * random.nextDouble(0.95, 1.05);
            }
            else if (probability < 15) {
                type = "KIRA_ODEMESI";
                amountRaw = -(baseSalary * random.nextDouble(0.20, 0.30));
            }
            else if (probability < 35) {
                type = "FATURA_ODEMESI";
                amountRaw = -(random.nextDouble(200.0, 1500.0));
            }
            else if (probability < 70) {
                type = "POS_HARCAMASI_MARKET";
                amountRaw = -(random.nextDouble(100.0, 800.0));
            }
            else {
                type = "POS_HARCAMASI_RESTORAN";
                amountRaw = -(random.nextDouble(300.0, 2000.0));
            }

            BigDecimal finalAmount = BigDecimal.valueOf(amountRaw).setScale(2, RoundingMode.HALF_UP);

            Transaction tx = Transaction.builder()
                    .account(account)
                    .amount(finalAmount)
                    .transactionType(type)
                    .createdAt(currentTxDate)
                    .build();

            transactions.add(tx);
        }

        return transactions;
    }
}