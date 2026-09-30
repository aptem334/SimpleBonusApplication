package com.bank.bonus.state;

import com.bank.bonus.model.Money;
import com.bank.bonus.processor.PaymentContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Обычная покупка онлайн: 17% бонусов.
 *
 * <p>Маршрутизация по сумме такая же, как в {@link RegularPaymentShopState}, но ставка выше.
 */
@Component
public class RegularPaymentOnlineState implements PaymentState {

    private static final BigDecimal RATE = Money.percent(17);

    private final EnhancedBonusState enhancedBonusState;
    private final BankCommissionState bankCommissionState;
    private final FinishPaymentState finishPaymentState;

    public RegularPaymentOnlineState(
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
            // 30% вместо 17%: по решению T-002 канал на enhanced-тарифе не влияет.
            return enhancedBonusState;
        }

        context.addBonus(Money.percentOf(amount, RATE));

        return amount.compareTo(BankCommissionState.THRESHOLD) < 0
                ? bankCommissionState
                : finishPaymentState;
    }
}
