package org.simplebonus.processor.state;

import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;

public interface PaymentProcessingState {
    default void processPayment(SimpleBonusInitials initials, PaymentProcessor processor) {

    }

}
