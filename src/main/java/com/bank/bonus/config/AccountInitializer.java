package com.bank.bonus.config;

import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Создаёт счёт клиента при старте.
 *
 * <p>Идемпотентен: если счёт уже есть, ничего не делает. Это важно для тестов,
 * где контекст поднимается один раз, а метод вызывается многократно.
 */
@Component
public class AccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AccountInitializer.class);

    private final AccountRepository accountRepository;
    private final InitialBalanceProperties properties;

    public AccountInitializer(AccountRepository accountRepository, InitialBalanceProperties properties) {
        this.accountRepository = accountRepository;
        this.properties = properties;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        accountRepository.findFirstByOrderByIdAsc().ifPresentOrElse(
                existing -> log.info("Счёт клиента уже существует, пропускаем инициализацию (id={})", existing.getId()),
                () -> {
                    CustomerAccount account = new CustomerAccount(
                            properties.money(), properties.bonus(), properties.bankCommission());
                    accountRepository.save(account);
                    log.info("Создан счёт клиента: money={}, bonus={}, bankCommission={}",
                            account.getMoney(), account.getBonus(), account.getBankCommission());
                });
    }
}
