package com.bank.bonus.state;

import com.bank.bonus.model.Money;
import com.bank.bonus.processor.PaymentContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Покупка дешевле 20 ₽: комиссия банку 10%.
 *
 * <p>Комиссия не отменяет бонус — состояние приходит уже после начисления
 * обычных 10%/17%, поэтому дешёвая покупка даёт и бонус, и комиссию.
 *
 * <p>Комиссия удерживается из денежного счёта, а не из бонусного: бонус
 * начисляется постфактум и не может быть источником средств для его же начисления.
 */
@Component
public class BankCommissionState implements PaymentState {

    /** Порог «меньше 20». Ровно 20.00 — уже не комиссия. */
    public static final BigDecimal THRESHOLD = new BigDecimal("20.00");

    private static final BigDecimal RATE = Money.percent(10);

    private final FinishPaymentState finishPaymentState;

    public BankCommissionState(FinishPaymentState finishPaymentState) {
        this.finishPaymentState = finishPaymentState;
    }

    @Override
    public PaymentState advance(PaymentContext context) {
        context.addCommission(Money.percentOf(context.amount(), RATE));
        return finishPaymentState;
    }
}
