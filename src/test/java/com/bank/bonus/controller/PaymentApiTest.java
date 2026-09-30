package com.bank.bonus.controller;

import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.model.PurchaseTransaction;
import com.bank.bonus.repository.AccountRepository;
import com.bank.bonus.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Сквозные тесты API: клиентские запросы → контроллер → сервис → состояния → БД.
 *
 * <p>Сценарии проверяют не «состояние сработало», а «клиент получил правильный ответ
 * и счёт сошёлся». Именно тут ловятся дефекты, которые юнит-тесты состояний не видят.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentApiTest {

    private static final BigDecimal START_MONEY = new BigDecimal("10000.00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void resetAccount() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountRepository.save(new CustomerAccount(START_MONEY, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    // ---------------------------------------------------------------- сценарии

    @Nested
    @DisplayName("Сценарная таблица: канал + сумма → бонус, комиссия, остаток")
    class Scenarios {

        @ParameterizedTest(name = "{0} {1} → бонус {2}, комиссия {3}, остаток {4}")
        @CsvSource({
                // обычные покупки
                "Shop,    100.00,   10.00,  0.00,  9900.00",
                "Online,  100.00,   17.00,  0.00,  9900.00",
                // граница комиссии: 20.00 — уже без комиссии
                "Shop,    19.99,     2.00,  2.00,  9978.01",
                "Shop,    20.00,     2.00,  0.00,  9980.00",
                "Online,  20.00,     3.40,  0.00,  9980.00",
                // дешёвые покупки: бонус И комиссия одновременно
                "Shop,    15.00,     1.50,  1.50,  9983.50",
                "Online,  15.00,     2.55,  1.50,  9983.50",
                // округление на некруглых суммах
                "Online,  199.99,   34.00,  0.00,  9800.01",
                // граница enhanced: ровно 300 — ещё обычный тариф
                "Shop,    299.99,   30.00,  0.00,  9700.01",
                "Shop,    300.00,   30.00,  0.00,  9700.00",
                "Online,  300.00,   51.00,  0.00,  9700.00",
                // 300.01 — уже 30% независимо от канала
                "Shop,    300.01,   90.00,  0.00,  9699.99",
                "Online,  400.00,  120.00,  0.00,  9600.00",
                // минимальная допустимая сумма: и бонус, и комиссия ненулевые
                "Shop,      0.10,    0.01,  0.01,  9999.89",
        })
        @DisplayName("бонусы, комиссия и остатки считаются по правилам")
        void purchaseScenario(String channel, String amount,
                              String expectedBonus, String expectedCommission, String expectedMoney) throws Exception {

            mockMvc.perform(get("/api/payment/{channel}/{amount}", channel, amount))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""));

            CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
            assertThat(account.getBonus()).isEqualByComparingTo(expectedBonus);
            assertThat(account.getBankCommission()).isEqualByComparingTo(expectedCommission);
            assertThat(account.getMoney()).isEqualByComparingTo(expectedMoney);

            List<PurchaseTransaction> history = transactionRepository.findAllByOrderByIdAsc();
            assertThat(history).hasSize(1);
            assertThat(history.getFirst().getBonus()).isEqualByComparingTo(expectedBonus);
            assertThat(history.getFirst().getCommission()).isEqualByComparingTo(expectedCommission);
        }
    }

    // ---------------------------------------------------------------- балансы

    @Test
    @DisplayName("Эндпоинты балансов отдают текущие значения")
    void balanceEndpointsReportCurrentState() throws Exception {
        mockMvc.perform(get("/api/money")).andExpect(status().isOk()).andExpect(content().string("10000.00"));
        mockMvc.perform(get("/api/bankAccountOfEMoney")).andExpect(status().isOk()).andExpect(content().string("0.00"));
        mockMvc.perform(get("/api/commission")).andExpect(status().isOk()).andExpect(content().string("0.00"));

        mockMvc.perform(get("/api/payment/{channel}/{amount}", "Shop", "15.00")).andExpect(status().isOk());

        mockMvc.perform(get("/api/money")).andExpect(content().string("9983.50"));
        mockMvc.perform(get("/api/bankAccountOfEMoney")).andExpect(content().string("1.50"));
        mockMvc.perform(get("/api/commission")).andExpect(content().string("1.50"));
    }

    @Test
    @DisplayName("Последовательные покупки накапливают бонусы")
    void sequentialPurchasesAccumulate() throws Exception {
        mockMvc.perform(get("/api/payment/Shop/100.00")).andExpect(status().isOk());
        mockMvc.perform(get("/api/payment/Online/200.00")).andExpect(status().isOk());
        mockMvc.perform(get("/api/payment/Shop/15.00")).andExpect(status().isOk());

        // 10.00 + 34.00 + 1.50
        mockMvc.perform(get("/api/bankAccountOfEMoney")).andExpect(content().string("45.50"));
        mockMvc.perform(get("/api/commission")).andExpect(content().string("1.50"));
        // 10000 - 100 - 200 - 15 - 1.50
        mockMvc.perform(get("/api/money")).andExpect(content().string("9683.50"));

        assertThat(transactionRepository.count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Канал в URL нечувствителен к регистру")
    void channelIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/payment/{channel}/{amount}", "shop", "100.00")).andExpect(status().isOk());
        mockMvc.perform(get("/api/payment/{channel}/{amount}", "ONLINE", "100.00")).andExpect(status().isOk());

        CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
        assertThat(account.getBonus()).isEqualByComparingTo("27.00");
    }

    // ---------------------------------------------------------------- ошибки

    @Nested
    @DisplayName("Ошибки отдаются как 400 с описанием")
    class Errors {

        @Test
        @DisplayName("нечисловая сумма")
        void nonNumericAmount() throws Exception {
            mockMvc.perform(get("/api/payment/Shop/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Некорректный формат суммы: 'abc'"));
        }

        @Test
        @DisplayName("отрицательная сумма")
        void negativeAmount() throws Exception {
            mockMvc.perform(get("/api/payment/Shop/-5"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Сумма покупки должна быть положительной: -5.00"));
        }

        @Test
        @DisplayName("нулевая сумма")
        void zeroAmount() throws Exception {
            mockMvc.perform(get("/api/payment/Shop/0.00")).andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("неизвестный канал")
        void unknownChannel() throws Exception {
            mockMvc.perform(get("/api/payment/{channel}/{amount}", "Market", "100.00"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Неизвестное место покупки: 'Market'. Ожидается Shop или Online"));
        }

        @Test
        @DisplayName("нехватка средств, счёт и история не меняются")
        void insufficientFunds() throws Exception {
            mockMvc.perform(get("/api/payment/Shop/99999.00"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Недостаточно средств: нужно 99999.00, доступно 10000.00"));

            CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
            assertThat(account.getMoney()).isEqualByComparingTo("10000.00");
            assertThat(account.getBonus()).isEqualByComparingTo("0.00");
            assertThat(transactionRepository.count()).isZero();
        }

        @Test
        @DisplayName("нехватка средств учитывает комиссию: 20.00 на счёте не хватает на покупку 19.00")
        void insufficientFundsIncludesCommission() throws Exception {
            accountRepository.deleteAll();
            accountRepository.save(new CustomerAccount(new BigDecimal("20.00"), BigDecimal.ZERO, BigDecimal.ZERO));

            mockMvc.perform(get("/api/payment/Shop/19.00"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Недостаточно средств: нужно 20.90, доступно 20.00"));
        }

        @Test
        @DisplayName("сумма с лишними знаками округляется на входе по HALF_UP (см. T-008)")
        void excessScaleIsRoundedOnInput() throws Exception {
            // 100.125 → 100.13 по HALF_UP, то есть списание на 2 копейки больше запроса.
            // Поведение зафиксировано тестом, но помечено тикетом T-008 как спорное.
            mockMvc.perform(get("/api/payment/Shop/100.125"))
                    .andExpect(status().isOk());

            CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
            assertThat(account.getMoney()).isEqualByComparingTo("9899.87");
            assertThat(account.getBonus()).isEqualByComparingTo("10.01");
        }
    }

    // ----------------------------------------------------------- минимальная сумма

    @Nested
    @DisplayName("Минимальная сумма покупки — 0.10 (T-010)")
    class MinimumAmount {

        @ParameterizedTest(name = "Shop {0} → HTTP 400")
        @ValueSource(strings = {"0.01", "0.05", "0.09"})
        @DisplayName("сумма ниже минимума отклоняется с указанием порога")
        void amountBelowMinimumIsRejected(String amount) throws Exception {
            mockMvc.perform(get("/api/payment/{channel}/{amount}", "Shop", amount))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(
                            "Сумма покупки меньше минимальной: %s, минимум 0.10".formatted(amount)));
        }

        @ParameterizedTest(name = "Shop {0} → HTTP 400 (округлено до нуля)")
        @ValueSource(strings = {"0.001", "0.004"})
        @DisplayName("суб-копеечная сумма округляется до нуля и отклоняется проверкой знака")
        void subKopeckAmountIsRejectedAsZero(String amount) throws Exception {
            // Побочный эффект решения округлять сумму на входе — см. T-008.
            // Проверка минимума сюда не доходит: контроллер уже превратил сумму в 0.00.
            mockMvc.perform(get("/api/payment/{channel}/{amount}", "Shop", amount))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Сумма покупки должна быть положительной: 0.00"));
        }

        @Test
        @DisplayName("ровно минимум проходит, бонус и комиссия ненулевые")
        void exactlyMinimumIsAccepted() throws Exception {
            mockMvc.perform(get("/api/payment/Shop/0.10"))
                    .andExpect(status().isOk());

            CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
            assertThat(account.getMoney()).isEqualByComparingTo("9999.89");
            assertThat(account.getBonus()).isEqualByComparingTo("0.01");
            assertThat(account.getBankCommission()).isEqualByComparingTo("0.01");
        }

        @Test
        @DisplayName("отклонённая сумма не оставляет ни денег, ни истории, ни бонусов")
        void rejectedAmountLeavesNoTrace() throws Exception {
            mockMvc.perform(get("/api/payment/Shop/0.01")).andExpect(status().isBadRequest());

            CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
            assertThat(account.getMoney()).isEqualByComparingTo("10000.00");
            assertThat(account.getBonus()).isEqualByComparingTo("0.00");
            assertThat(transactionRepository.count()).isZero();
        }

        @Test
        @DisplayName("минимальная сумма не обходит проверку баланса")
        void minimumStillRequiresMoney() throws Exception {
            accountRepository.deleteAll();
            accountRepository.save(new CustomerAccount(new BigDecimal("0.05"), BigDecimal.ZERO, BigDecimal.ZERO));

            mockMvc.perform(get("/api/payment/Shop/0.10"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Недостаточно средств: нужно 0.11, доступно 0.05"));
        }
    }
}
