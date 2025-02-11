package org.simplebonus.processor.state;

import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

import static org.simplebonus.constant.Constants.PERCENT;

@Slf4j
public class RegularPaymentOnlineState extends AbstractRegularPaymentState {

    @Override
    public void processPayment(SimpleBonusInitials initials, PaymentProcessor processor) {
        // рассчитываем бонус клиента
        BigDecimal bonus = processor.getAmount()
                .multiply(initials.getBonusRegularOnline())
                .divide(BigDecimal.valueOf(PERCENT));
        processor.setBonus(bonus);
        // следующее состояние не является однозначным и зависит
        // от величины суммы покупки и места, где она совершается
        log.info("Начислен бонус за онлайн-покупку: {} рублей", bonus);
        processor.changeState(calculateNewState(initials, processor));
    }

    @Override
    public String toString() {
        return "Покупка онлайн";
    }
}
