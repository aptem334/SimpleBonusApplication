package org.simplebonus.processor.state;

import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;

import java.math.BigDecimal;


public class InitPaymentState implements PaymentProcessingState {
    @Override
    public void processPayment(SimpleBonusInitials initials, PaymentProcessor processor) {
        processor.setBonus(BigDecimal.ZERO);
        processor.setCommission(BigDecimal.ZERO);

        PaymentProcessingState newState = ("Shop".equals(processor.getPlace()))
                ? new RegularPaymentShopState()
                : new RegularPaymentOnlineState();
        processor.changeState(newState);
    }
}
