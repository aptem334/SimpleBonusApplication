package com.bank.bonus.config;

import com.bank.bonus.model.Money;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Пороги бизнес-логики, задаваемые извне.
 *
 * <p>Отдельная запись от {@link InitialBalanceProperties}, потому что это разные вещи:
 * там — с чего стартуем, здесь — что вообще разрешено.
 *
 * <p>Минимальная сумма существует из-за <b>T-010</b>: при сумме меньше 0.05 ₽
 * и бонус, и комиссия округляются до нуля, то есть деньги списываются,
 * а пользы нет ни клиенту, ни банку.
 */
@ConfigurationProperties(prefix = "app")
public record AppRulesProperties(BigDecimal minAmount) {

    /**
     * Дублируется из {@code application.yml} намеренно: чтобы при запуске
     * без конфигурации сервис всё равно не разрешал операции-пустышки.
     */
    public static final BigDecimal DEFAULT_MIN_AMOUNT = new BigDecimal("0.10");

    public AppRulesProperties {
        if (minAmount == null) {
            minAmount = DEFAULT_MIN_AMOUNT;
        } else {
            // Приводим к денежной точности, потому что порог попадает в текст ошибки.
            // Без этого `0.1` и `0.10` печатались бы по-разному, хотя это одна сумма,
            // и сравнение строк в тестах ловило бы разницу там, где её нет по сути.
            minAmount = minAmount.setScale(Money.SCALE, RoundingMode.UNNECESSARY);
        }
    }
}
