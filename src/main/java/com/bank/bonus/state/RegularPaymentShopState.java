package com.bank.bonus.state;

import com.bank.bonus.model.Money;
import com.bank.bonus.processor.PaymentContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Обычная покупка в мазине: 10% бонусов.
 *
 * <p>Это состояние-маршрутизатор по сумме: оно решает, какой тариф применять.
 * Пороги лежат в соседних состояниях ({@link EnhancedBonusState#THRESHOLD},
 * {@link BankCommissionState#THRESHOLD}), чтобы правило «больше 300» и правило
 * «меньше 20» каждое жило ровно в одном классе и не расходилось между копиями.
 */
@Component
public class RegularPaymentShopState implements PaymentState {

    private static final BigDecimal RATE = Money.percent(10);

    private final EnhancedBonusState enhancedBonusState;
    private final BankCommissionState bankCommissionState;
    private final FinishPaymentState finishPaymentState;

    public RegularPaymentShopState(
            EnhancedBonusState enhancedBonusState,
            BankCommissionState bankCommissionState,
            FinishPaymentState finishPaymentState) {
        this.enhancedBonusState = enhancedBonusState;
        this.bankCommissionState = bankCommissionState;
        this.finishPaymentState = finishPaymentState;
    }

    @Override
    public PaymentState advance(PaymentContext context) {
        BigDecimal amount = context.amount();

        if (amount.compareTo(EnhancedBonusState.THRESHOLD) > 0) {
            // 30% вместо 10%: обычный бонус магазина не начисляется вовсе.
            return enhancedBonusState;
        }

        context.addBonus(Money.percentOf(amount, RATE));

        return amount.compareTo(BankCommissionState.THRESHOLD) < 0
                ? bankCommissionState
                : finishPaymentState;
    }
}
