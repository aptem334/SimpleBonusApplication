package org.simplebonus.processor.state;

import lombok.extern.slf4j.Slf4j;
import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;

import java.math.BigDecimal;

import static org.simplebonus.constant.Constants.PERCENT;

@Slf4j
public class RegularPaymentShopState extends AbstractRegularPaymentState {

    @Override
    public void processPayment(SimpleBonusInitials initials, PaymentProcessor processor) {
        // рассчитываем бонус клиента
        BigDecimal bonus = processor.getAmount()
                .multiply(initials.getBonusRegularShop())
                .divide(BigDecimal.valueOf(PERCENT));
        processor.setBonus(bonus);
        log.info("Начислен бонус за покупку в магазине: {} рублей", bonus);

        // следующее состояние не является однозначным и зависит
        // от величины суммы покупки и места, где она совершается
        processor.changeState(calculateNewState(initials, processor));
    }

    @Override
    public String toString() {
        return "Покупка в магазине";
    }
}
