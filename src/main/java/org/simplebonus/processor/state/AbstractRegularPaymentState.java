package org.simplebonus.processor.state;

import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;

import java.math.BigDecimal;

public abstract class AbstractRegularPaymentState implements PaymentProcessingState {
    private boolean isEnhanced(BigDecimal threshold, BigDecimal amount) {
        return amount.compareTo(threshold) > 0;
    }

    private boolean isCommission(BigDecimal threshold, BigDecimal amount) {
        return amount.compareTo(threshold) < 0;
    }

    protected PaymentProcessingState calculateNewState(SimpleBonusInitials initials, PaymentProcessor processor) {
        /*
        Выбор следующего состояния зависит от величины суммы покупки:
        1) сумма над верхним пределом, заданным для регулярных покупок, переводит в состояние начисления повышенного бонуса
        2) сумма под нижним переделом, заданным для регулярных покупок, переводит в состояние нулевого бонуса и начисления
        комиссии в пользу банка
        3) в остальных случаях завершаем обработку платежа уже на этом шаге
        */
        return isEnhanced(initials.getEnhancedBonusThreshold(), processor.getAmount())
                ? new EnhancedBonusState()
                : (isCommission(initials.getBankCommissionThreshold(), processor.getAmount())
                ? new BankCommissionState()
                : new FinishPaymentState());
    }
}
