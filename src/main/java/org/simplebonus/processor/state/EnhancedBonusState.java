package org.simplebonus.processor.state;

import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

import static org.simplebonus.constant.Constants.PERCENT;

@Slf4j
public class EnhancedBonusState implements PaymentProcessingState {
    @Override
    public void processPayment(SimpleBonusInitials initials, PaymentProcessor processor) {
        // Клиент получает увеличенный бонус вне зависимости от места совершения покупки
        BigDecimal bonus = processor.getAmount()
                .multiply(initials.getBonusEnhanced())
                .divide(BigDecimal.valueOf(PERCENT));
        processor.setBonus(bonus);
        log.info("Начислен повышенный бонус: {} рублей", bonus);
        processor.changeState(new FinishPaymentState());
    }

    @Override
    public String toString() {
        return "Покупка выше максимального порога стандартной, клиент получает увеличенный бонус";
    }
}
