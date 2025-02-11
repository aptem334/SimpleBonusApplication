package org.simplebonus.processor.state;

public class FinishPaymentState implements PaymentProcessingState {
    @Override
    public String toString() {
        return "Обработка платежа завершена";
    }
}
