package com.bank.bonus.state;

import com.bank.bonus.model.Money;
import com.bank.bonus.processor.PaymentContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Покупка дороже 300 ₽: 30% бонусов.
 *
 * <p>Канал покупки здесь намеренно не читается: по решению T-002 «>300» — это
 * особый тариф, который заменяет обычные 10/17%. Состояния-мастера (обычные
 * Shop/Online) проверяют порог и передают платёж сюда до начисления своего бонуса.
 */
@Component
public class EnhancedBonusState implements PaymentState {

    /** Порог «больше 300». Ровно 300.00 — ещё не enhanced. */
    public static final BigDecimal THRESHOLD = new BigDecimal("300.00");

    private static final BigDecimal RATE = Money.percent(30);

    private final FinishPaymentState finishPaymentState;

    public EnhancedBonusState(FinishPaymentState finishPaymentState) {
        this.finishPaymentState = finishPaymentState;
    }

    @Override
    public PaymentState advance(PaymentContext context) {
        context.addBonus(Money.percentOf(context.amount(), RATE));
        return finishPaymentState;
    }
}
