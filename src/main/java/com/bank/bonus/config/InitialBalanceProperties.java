package com.bank.bonus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Стартовые балансы клиента. Вынесены в конфиг, а не зашиты в код:
 * тестам нужен предсказуемый старт, а вручную поправить баланс проще,
 * чем перекомпилировать.
 */
@ConfigurationProperties(prefix = "app.initial")
public record InitialBalanceProperties(
        BigDecimal money,
        BigDecimal bonus,
        BigDecimal bankCommission) {

    public InitialBalanceProperties {
        money = money == null ? BigDecimal.ZERO : money;
        bonus = bonus == null ? BigDecimal.ZERO : bonus;
        bankCommission = bankCommission == null ? BigDecimal.ZERO : bankCommission;
    }
}
