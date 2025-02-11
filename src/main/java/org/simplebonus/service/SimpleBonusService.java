package org.simplebonus.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.simplebonus.config.SimpleBonusInitials;
import org.simplebonus.exception.InsufficientClientMoneyException;
import org.simplebonus.exception.WrongInputDataFormatException;
import org.simplebonus.model.entity.PaymentPlace;
import org.simplebonus.model.entity.SinglePaymentEntity;
import org.simplebonus.processor.PaymentProcessor;
import org.simplebonus.repository.PaymentHistoryRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Сервис для обработки платежей.
 */

@Component
@AllArgsConstructor
public class SimpleBonusService {
    private static final int OUTPUT_SCALE = 2;

    private final SimpleBonusInitials simpleBonusInitials;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public String processPayment(PaymentPlace place, String amountStr) {
        // рассчитываем текущий баланс клиента: если не было ни одной сделки,
        // то берем из начальных условий приложения, иначе - из последней сделки
        BigDecimal moneyBefore = (paymentHistoryRepository.count() > 0)
                ? paymentHistoryRepository.findFirstByOrderByIdDesc().getClientMoneyAfter()
                : simpleBonusInitials.getClientInitialMoney();
        // проверяем корректность рассчитанного баланса и входящей суммы покупки
        BigDecimal amount = validateAndConvert(moneyBefore, amountStr);

        PaymentProcessor processor = new PaymentProcessor();
        SinglePaymentEntity singlePayment = processor.processPayment(simpleBonusInitials, place, amount);
        // дополняем объект для новой записи базы данных на основе бонусов,
        // высчитанных за время движения по состояниям процесса обработки
        singlePayment.setClientMoneyBefore(moneyBefore);
        singlePayment.setClientMoneyAfter(moneyBefore.subtract(amount).add(singlePayment.getClientBonus()));

        paymentHistoryRepository.save(singlePayment);
        return "Оплата успешна. Начислено бонусов: " + singlePayment.getClientBonus();
    }

    public BigDecimal getClientAccruedBonuses() {
        return paymentHistoryRepository.count() > 0
                ? Optional.ofNullable(paymentHistoryRepository.sumAccruedBonuses())
                                .orElse(BigDecimal.ZERO)
                                .setScale(OUTPUT_SCALE, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(OUTPUT_SCALE);
    }

    public BigDecimal getBankCommissionTotal() {
        return paymentHistoryRepository.count() > 0
                ? Optional.ofNullable(paymentHistoryRepository.sumBankCommissions())
                                .orElse(BigDecimal.ZERO)
                                .setScale(OUTPUT_SCALE, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(OUTPUT_SCALE);
    }

    public BigDecimal getClientAvailableMoney() {
        return paymentHistoryRepository.count() > 0
                ? paymentHistoryRepository.findFirstByOrderByIdDesc().getClientMoneyAfter().setScale(OUTPUT_SCALE, RoundingMode.HALF_UP)
                : simpleBonusInitials.getClientInitialMoney().setScale(OUTPUT_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal validateAndConvert(BigDecimal moneyBefore, String amountStr) {
        BigDecimal amountToPay;
        // отправляем в ответ понятное сообщение в случае некорректного задания суммы покупки:
        // например, при использовании неподдреживаемого разделителя целой и дробной части
        if (NumberUtils.isParsable(amountStr)) {
            amountToPay = NumberUtils.createBigDecimal(amountStr);
        } else {
            throw new WrongInputDataFormatException(amountStr);
        }

        if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientClientMoneyException("задана некорректная сумма покупки");
        }
        if (moneyBefore.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientClientMoneyException("нет средств на счете");
        }
        if (moneyBefore.compareTo(amountToPay) < 0) {
            throw new InsufficientClientMoneyException("сумма покупки превышает остаток на счете");
        }

        return amountToPay;
    }
}
