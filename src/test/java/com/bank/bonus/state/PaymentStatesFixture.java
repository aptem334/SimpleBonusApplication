package com.bank.bonus.state;

import com.bank.bonus.config.AppRulesProperties;
import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.processor.PaymentContext;
import com.bank.bonus.repository.TransactionRepository;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;

/**
 * Настоящий граф состояний для юнит-тестов.
 *
 * <p>Собирается вручную, а не берётся из Spring-контекста: тестам нужно проверить
 * переходы между конкретными состояниями, а для этого надо владеть ссылками на них.
 * Заглушки-лямбды здесь не работают — конструкторы состояний принимают конкретные
 * типы намеренно (см. {@link PaymentState}).
 */
final class PaymentStatesFixture {

    /** Порог из application.yml — тот же, что и в рабочем приложении. */
    static final AppRulesProperties RULES = new AppRulesProperties(new BigDecimal("0.10"));

    final TransactionRepository repository = mock(TransactionRepository.class);
    final FinishPaymentState finish = new FinishPaymentState(repository);
    final EnhancedBonusState enhanced = new EnhancedBonusState(finish);
    final BankCommissionState commission = new BankCommissionState(finish);
    final RegularPaymentShopState shop = new RegularPaymentShopState(enhanced, commission, finish);
    final RegularPaymentOnlineState online = new RegularPaymentOnlineState(enhanced, commission, finish);
    final InitPaymentState init = new InitPaymentState(shop, online, RULES);

    /** Контекст с указанной суммой покупки и балансами. */
    static PaymentContext context(String amount, String money, String bonus, String bankCommission) {
        return new PaymentContext(null, new BigDecimal(amount),
                new CustomerAccount(new BigDecimal(money), new BigDecimal(bonus), new BigDecimal(bankCommission)));
    }

    /** Контекст с пустым счётом — когда значения счёта в тесте не важны. */
    static PaymentContext context(String amount) {
        return context(amount, "0.00", "0.00", "0.00");
    }
}
