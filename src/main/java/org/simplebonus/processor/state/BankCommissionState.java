package org.simplebonus.processor.state;

import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.processor.PaymentProcessor;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;

import static org.simplebonus.constant.Constants.PERCENT;

@Slf4j
public class BankCommissionState implements PaymentProcessingState {

    @Override
    public void processPayment(SimpleBonusInitials initials, PaymentProcessor processor) {
        // поскольку приходим в это состояние после попытки обработать покупку как регулярную,
        // очищаем ранее рассчитанный клиентский бонус
        processor.setBonus(BigDecimal.ZERO);
        // рассчитываем коммиссию банка
        BigDecimal commission = processor.getAmount()
                .multiply(initials.getBankCommissionPercent())
                .divide(BigDecimal.valueOf(PERCENT));
        processor.setCommission(commission);
        log.info("Списана комиссия банка: {} рублей", commission);
        processor.changeState(new FinishPaymentState());
    }

    @Override
    public String toString() {
        return "Покупка ниже минимального порога стандартной, банк получает коммиссию";
    }
}
