package org.simplebonus.processor;

import lombok.extern.slf4j.Slf4j;
import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.model.entity.PaymentPlace;
import org.simplebonus.model.entity.SinglePaymentEntity;
import org.simplebonus.processor.state.InitPaymentState;
import org.simplebonus.processor.state.PaymentProcessingState;

import java.math.BigDecimal;

@Slf4j
public class PaymentProcessor {
    PaymentProcessingState processingState;
    SinglePaymentEntity paymentEntity;
    SimpleBonusInitials initials;

    public SinglePaymentEntity processPayment(SimpleBonusInitials initials, PaymentPlace place, BigDecimal amount) {
        log.info("Начало обработки платежа");
        this.initials = initials;
        paymentEntity = SinglePaymentEntity.builder().build();
        paymentEntity.setPlace(place);
        paymentEntity.setAmount(amount);

        // Обработка платежа ставится в начальное состояние, и по окончании обработки ждем
        // заполненные поля для клиентского бонуса и комиссии банка
        processingState = new InitPaymentState();
        processingState.processPayment(initials, this);

        return paymentEntity;
    }

    public void changeState(PaymentProcessingState newState) {
        log.info("Состояние обработки платежа меняется, новое состояние: {}", newState);
        processingState = newState;
        processingState.processPayment(initials,this);
    }

    public String getPlace() {
        return paymentEntity.getPlace().name();
    }

    public BigDecimal getAmount() {
        return paymentEntity.getAmount();
    }

    public void setBonus(BigDecimal bonus) {
        paymentEntity.setClientBonus(bonus);
    }

    public void setCommission(BigDecimal commission) {
        paymentEntity.setBankCommission(commission);
    }
}
