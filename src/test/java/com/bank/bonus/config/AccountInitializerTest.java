package com.bank.bonus.config;

import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.repository.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "app.initial.money=500.00",
        "app.initial.bonus=7.50",
        "app.initial.bank-commission=1.25"
})
class AccountInitializerTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("При старте создаётся ровно один счёт с балансами из конфига")
    void accountIsSeededFromConfiguration() {
        assertThat(accountRepository.count()).isEqualTo(1);

        CustomerAccount account = accountRepository.findFirstByOrderByIdAsc().orElseThrow();
        assertThat(account.getMoney()).isEqualByComparingTo("500.00");
        assertThat(account.getBonus()).isEqualByComparingTo("7.50");
        assertThat(account.getBankCommission()).isEqualByComparingTo("1.25");
    }

    @Test
    @DisplayName("Повторный запуск инициализатора не создаёт второй счёт")
    void initializationIsIdempotent() {
        // Первый вызов уже произошёл при поднятии контекста.
        new AccountInitializer(accountRepository,
                new InitialBalanceProperties(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO))
                .run(null);

        assertThat(accountRepository.count()).isEqualTo(1);
    }
}
