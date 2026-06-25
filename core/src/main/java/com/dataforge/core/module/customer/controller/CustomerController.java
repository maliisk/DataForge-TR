package com.dataforge.core.module.customer.controller;

import com.dataforge.core.module.account.repository.AccountRepository;
import com.dataforge.core.module.customer.entity.Customer;
import com.dataforge.core.module.customer.repository.CreditCardRepository;
import com.dataforge.core.module.customer.repository.CustomerRepository;
import com.dataforge.core.module.customer.repository.LoanRepository;
import com.dataforge.core.module.transaction.repository.TransactionRepository;
import com.dataforge.core.payload.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CreditCardRepository creditCardRepository;
    private final LoanRepository loanRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Customer>>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Customer> customerPage = customerRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))
        );

        ApiResponse<Page<Customer>> response = ApiResponse.<Page<Customer>>builder()
                .success(true)
                .message("Müşteri listesi başarıyla getirildi.")
                .data(customerPage)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearDatabase() {
        transactionRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
        creditCardRepository.deleteAllInBatch();
        loanRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Tüm test havuzu (Müşteriler, Hesaplar, Kartlar, Krediler ve İşlemler) başarıyla sıfırlandı.")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/export/csv", produces = "text/csv; charset=utf-8")
    public ResponseEntity<StreamingResponseBody> exportAllToCsv() {

        StreamingResponseBody stream = out -> {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
                writer.write("\uFEFF");
                writer.write("Musteri_ID;Tip;Kimlik_VKN;Ad_Unvan;Dogum_Sektor;Risk_Skoru;Kart_Sayisi;Kredi_Var_Mi;IBAN;Bakiye\n");

                int page = 0;
                int size = 1000;
                Page<Customer> customerPage;

                do {
                    customerPage = customerRepository.findAll(PageRequest.of(page, size));

                    for (Customer c : customerPage.getContent()) {
                        boolean isCorp = c.getCustomerType().name().equals("CORPORATE");
                        String identity = isCorp ? c.getTaxNumber() : c.getTckn();
                        String name = isCorp ? c.getCompanyName() : (c.getFirstName() + " " + c.getLastName());
                        String subInfo = isCorp ? c.getSector() : (c.getBirthDate() != null ? c.getBirthDate().toString() : "");
                        String cardCount = c.getCreditCards() != null ? String.valueOf(c.getCreditCards().size()) : "0";
                        String hasLoan = (c.getLoans() != null && !c.getLoans().isEmpty()) ? "EVET" : "HAYIR";
                        String iban = (c.getAccounts() != null && !c.getAccounts().isEmpty()) ? c.getAccounts().get(0).getIban() : "YOK";
                        String balance = (c.getAccounts() != null && !c.getAccounts().isEmpty()) ? c.getAccounts().get(0).getBalance().toString() : "0";

                        writer.write(c.getId() + ";" + c.getCustomerType() + ";" + identity + ";" +
                                name + ";" + subInfo + ";" + c.getRiskScore() + ";" +
                                cardCount + ";" + hasLoan + ";" + iban + ";" + balance + "\n");
                    }
                    writer.flush();
                    page++;
                } while (customerPage.hasNext());
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dataforge_customers.csv")
                .body(stream);
    }
}