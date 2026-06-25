package com.dataforge.core.module.generator.service;

import com.dataforge.core.config.RabbitMqConfig;
import com.dataforge.core.module.account.entity.Account;
import com.dataforge.core.module.customer.entity.Customer;
import com.dataforge.core.module.customer.entity.CustomerType;
import com.dataforge.core.module.customer.entity.CreditCard;
import com.dataforge.core.module.customer.entity.Loan;
import com.dataforge.core.module.customer.repository.CustomerRepository;
import com.dataforge.core.module.generator.util.IbanGenerator;
import com.dataforge.core.module.generator.util.TcknGenerator;
import com.dataforge.core.payload.request.GenerateBatchRequest;
import com.dataforge.core.payload.request.ScenarioType;
import com.dataforge.core.module.transaction.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntheticDataService {

    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Faker faker = new Faker(new Locale("tr"));

    private static final String[] NAMES = {
            "Ahmet", "Mehmet", "Mustafa", "Ali", "Hüseyin", "Hasan", "İbrahim", "İsmail", "Osman", "Yusuf",
            "Murat", "Ömer", "Ramazan", "Halil", "Süleyman", "Abdullah", "Mahmut", "Salih", "Recep", "Kadir",
            "Can", "Burak", "Kaan", "Cem", "Deniz", "Gökhan", "Yasin", "Oğuzhan", "Kerem", "Okan",
            "Kemal", "Kenan", "Tolga", "Alper", "Volkan", "Serkan", "Hakan", "Orhan", "Tarık", "Erdem",
            "Onur", "Umut", "Uğur", "Ozan", "Mert", "Berk", "Efe", "Emir", "Eren", "Alp",
            "Arda", "Baran", "Batu", "Cihan", "Çağlar", "Engin", "Enes", "Ersin", "Fatih", "Furkan",
            "İlker", "Koray", "Levent", "Metin", "Özgür", "Sefa", "Sinan", "Şahin", "Şükrü", "Tahir",
            "Taha", "Tayfun", "Ufuk", "Veli", "Yavuz", "Zafer", "Alperen", "Bora", "Doğu", "Ege",
            "Ayşe", "Fatma", "Zeynep", "Hatice", "Emine", "Elif", "Merve", "Büşra", "Kübra", "Yasemin",
            "Zehra", "Hacer", "Esra", "Hale", "Aynur", "Sevgi", "Sultan", "Hülya", "Aysel", "Tuğba",
            "Gizem", "Ceren", "Gözde", "Cansu", "Selin", "Aslı", "Ayşegül", "Aylin", "Banu", "Başak",
            "Berna", "Betül", "Beyza", "Buket", "Burcu", "Buse", "Ceyda", "Çağla", "Çiğdem", "Damla",
            "Derya", "Didem", "Dilara", "Duygu", "Ebru", "Eda", "Ezgi", "Funda", "Gamze", "Gonca",
            "Gül", "Hande", "Hazal", "Hilal", "Işıl", "İlayda", "İrem", "Melis", "Meltem", "Neslihan",
            "Nida", "Nil", "Nur", "Özge", "Pelin", "Pınar", "Rabia", "Seda", "Sena", "Sibel",
            "Sinem", "Şeyma", "Tuğçe", "Ece", "Melike", "Cemre", "Bade", "Doğa", "Su", "Nehir"
    };

    private static final String[] SURNAMES = {
            "Yılmaz", "Kaya", "Demir", "Çelik", "Şahin", "Yıldız", "Yıldırım", "Öztürk", "Aydın", "Özdemir",
            "Arslan", "Doğan", "Kılıç", "Aslan", "Çetin", "Kara", "Koç", "Kurt", "Özkan", "Şimşek",
            "Polat", "Öz", "Erdoğan", "Yavuz", "Koçak", "Can", "Acar", "Avcı", "Güler", "Ateş",
            "Güneş", "Bozkurt", "Işık", "Keser", "Tekin", "Aksoy", "Bulut", "Coşkun", "Çakar", "Çam",
            "Çevik", "Dalkılıç", "Demirci", "Dinç", "Doğanay", "Duman", "Ekinci", "Er", "Ercan", "Erdem",
            "Eren", "Ersoy", "Ertürk", "Fidan", "Genç", "Gök", "Gökalp", "Gündüz", "Güngör", "Güven",
            "İnce", "Kahraman", "Kaplan", "Karaca", "Karadaş", "Karakaya", "Karaman", "Kısa", "Kocaman", "Korkmaz",
            "Köksal", "Köse", "Mutlu", "Orhan", "Ozan", "Özer", "Özgül", "Parlak", "Sağlam", "Sarı",
            "Savaş", "Sezer", "Soylu", "Şen", "Şenol", "Taşkın", "Tezcan", "Topal", "Toprak", "Tosun",
            "Tunç", "Turan", "Turgut", "Uçar", "Uğur", "Ulusoy", "Uysal", "Uzun", "Varol", "Vural",
            "Yalçın", "Yaman", "Yanık", "Yener", "Yeşilyurt", "Yolcu", "Yücel", "Yüksel", "Zorlu", "Alp",
            "Batur", "Cevher", "Çağlar", "Dağ", "Efe", "Gül", "Has", "Ilıcalı", "İlhan", "Kar",
            "Lale", "Meriç", "Narlı", "Olcay", "Önal", "Peker", "Reis", "Sönmez", "Tuncel", "Ulu"
    };

    @Transactional
    public Customer generateCustomerByScenario(ScenarioType scenario) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        CustomerType type = random.nextInt(100) < 20 ? CustomerType.CORPORATE : CustomerType.INDIVIDUAL;

        int riskScore;
        double minBalance, maxBalance;

        switch (scenario) {
            case AML_SUSPICIOUS:
                riskScore = random.nextInt(85, 101);
                minBalance = 500000; maxBalance = 2000000;
                break;
            case HIGH_RISK_CUSTOMER:
                riskScore = random.nextInt(51, 84);
                minBalance = 5000; maxBalance = 15000;
                break;
            case NORMAL_CUSTOMER:
            default:
                riskScore = random.nextInt(0, 50);
                minBalance = 10000; maxBalance = 80000;
                break;
        }

        Customer.CustomerBuilder customerBuilder = Customer.builder()
                .customerType(type)
                .riskScore(riskScore);

        if (type == CustomerType.INDIVIDUAL) {
            customerBuilder
                    .tckn(TcknGenerator.generateValidTckn())
                    .firstName(NAMES[random.nextInt(NAMES.length)])
                    .lastName(SURNAMES[random.nextInt(SURNAMES.length)])
                    .birthDate(LocalDate.now().minusYears(random.nextInt(18, 65)));
        } else {
            customerBuilder
                    .taxNumber(String.format("%010d", random.nextLong(1000000000L, 9999999999L)))
                    .companyName(faker.company().name() + " A.Ş.")
                    .sector(faker.company().industry());
        }

        Customer customer = customerBuilder.build();

        Account account = Account.builder()
                .customer(customer)
                .iban(IbanGenerator.generateTrIban("00010"))
                .balance(BigDecimal.valueOf(random.nextDouble(minBalance, maxBalance)))
                .currency("TRY")
                .status("ACTIVE")
                .build();

        List<Transaction> transactions = new ArrayList<>();
        if (scenario == ScenarioType.AML_SUSPICIOUS) {
            LocalDateTime suspiciousTime = LocalDateTime.now().withHour(3).withMinute(random.nextInt(0, 59));
            for (int i = 0; i < 20; i++) {
                transactions.add(Transaction.builder()
                        .amount(BigDecimal.valueOf(random.nextDouble(45000, 49999)))
                        .transactionType("OUTGOING_EFT")
                        .createdAt(suspiciousTime.plusMinutes(i * 2L))
                        .account(account)
                        .build());
            }
        } else {
            for (int m = 1; m <= 3; m++) {
                LocalDateTime monthDate = LocalDateTime.now().minusMonths(m);

                transactions.add(Transaction.builder()
                        .amount(BigDecimal.valueOf(random.nextDouble(30000, 80000)))
                        .transactionType("GELEN_MAAS_EFT")
                        .createdAt(monthDate.withDayOfMonth(1).withHour(9))
                        .account(account).build());

                transactions.add(Transaction.builder()
                        .amount(BigDecimal.valueOf(random.nextDouble(5000, 20000)))
                        .transactionType("KREDI_KARTI_ODEMESI")
                        .createdAt(monthDate.withDayOfMonth(5).withHour(14))
                        .account(account).build());

                for(int i = 0; i < random.nextInt(3, 8); i++) {
                    transactions.add(Transaction.builder()
                            .amount(BigDecimal.valueOf(random.nextDouble(100, 3000)))
                            .transactionType("POS_HARCAMASI_" + (random.nextBoolean() ? "MARKET" : "RESTORAN"))
                            .createdAt(monthDate.withDayOfMonth(random.nextInt(6, 28)).withHour(random.nextInt(10, 22)))
                            .account(account).build());
                }
            }
        }
        account.setTransactions(transactions);

        List<CreditCard> creditCards = new ArrayList<>();
        int cardCount = random.nextInt(1, 3);
        for (int i = 0; i < cardCount; i++) {
            BigDecimal limit = BigDecimal.valueOf(random.nextDouble(10000, 100000));
            String rawCardNumber = faker.finance().creditCard().replaceAll("[^0-9]", "");
            String finalCardNumber = rawCardNumber.length() > 16 ? rawCardNumber.substring(0, 16) : String.format("%-16s", rawCardNumber).replace(' ', '0');

            creditCards.add(CreditCard.builder()
                    .cardNumber(finalCardNumber)
                    .cardLimit(limit)
                    .currentDebt(limit.multiply(BigDecimal.valueOf(random.nextDouble(0.1, 0.9))))
                    .expiryDate(String.format("%02d/%d", random.nextInt(1, 13), random.nextInt(26, 32)))
                    .cvv(String.format("%03d", random.nextInt(100, 999)))
                    .customer(customer)
                    .build());
        }

        List<Loan> loans = new ArrayList<>();
        if (random.nextInt(100) < 30) {
            BigDecimal totalLoan = BigDecimal.valueOf(random.nextDouble(50000, 500000));
            loans.add(Loan.builder()
                    .totalAmount(totalLoan)
                    .remainingDebt(totalLoan.multiply(BigDecimal.valueOf(random.nextDouble(0.4, 0.9))))
                    .interestRate(random.nextDouble(1.89, 4.59))
                    .installmentCount(random.nextInt(12, 48))
                    .startDate(LocalDate.now().minusMonths(random.nextInt(1, 10)))
                    .customer(customer)
                    .build());
        }

        customer.setAccounts(List.of(account));
        customer.setCreditCards(creditCards);
        customer.setLoans(loans);

        Customer savedCustomer = customerRepository.save(customer);

        String identifier = (type == CustomerType.INDIVIDUAL) ? savedCustomer.getTckn() : savedCustomer.getTaxNumber();
        log.info("Sentetik Müşteri Üretildi. Tip: {}, Senaryo: {}, ID/VergiNo: {}",
                type, scenario, identifier);

        return savedCustomer;
    }

    public void publishBatchRequest(GenerateBatchRequest request) {
        log.info("{} adet müşteri (Senaryo: {}) üretimi için talep alındı, kuyruğa iletiliyor...",
                request.getCustomerCount(), request.getScenario());
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY, request);
    }
}