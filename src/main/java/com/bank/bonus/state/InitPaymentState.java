package com.bank.bonus.state;

import com.bank.bonus.config.AppRulesProperties;
import com.bank.bonus.exception.InvalidAmountException;
import com.bank.bonus.exception.UnknownChannelException;
import com.bank.bonus.model.PurchaseChannel;
import com.bank.bonus.processor.PaymentContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Начальное состояние: проверяет вход и выбирает ветку по каналу покупки.
 *
 * <p>Проверки дублируют то, что контроллер уже сделал при разборе URL. Это осознанно:
 * сервис могут вызывать и из тестов, и из будущего CLI-клиента, минуя HTTP.
 * Контракт состояния не должен зависеть от того, кто вызвал.
 */
@Component
public class InitPaymentState implements PaymentState {

    private final RegularPaymentShopState shopState;
    private final RegularPaymentOnlineState onlineState;
    private final BigDecimal minAmount;

    public InitPaymentState(
            RegularPaymentShopState shopState,
            RegularPaymentOnlineState onlineState,
            AppRulesProperties rules) {
        this.shopState = shopState;
        this.onlineState = onlineState;
        this.minAmount = rules.minAmount();
    }

    @Override
    public PaymentState advance(PaymentContext context) {
        if (context.channel() == null) {
            throw new UnknownChannelException("Неизвестное место покупки. Ожидается Shop или Online");
        }
        if (context.amount() == null) {
            throw new InvalidAmountException("Сумма покупки не указана");
        }

        // Знак проверяется ПЕРЕД минимумом намеренно. Отрицательная сумма — это не
        // «слишком мало», это другая ошибка, и сообщение должно её называть прямо.
        //
        // Побочный эффект: сумма 0.001 округляется контроллером до 0.00 и попадает сюда
        // нулём, поэтому отвергается с сообщением про 0.00. Это плата за решение
        // округлять сумму на входе, а не на границе расчёта — сам вопрос открыт в T-008.
        if (context.amount().signum() <= 0) {
            throw new InvalidAmountException(
                    "Сумма покупки должна быть положительной: " + context.amount().toPlainString());
        }
        if (context.amount().compareTo(minAmount) < 0) {
            throw new InvalidAmountException(
                    "Сумма покупки меньше минимальной: %s, минимум %s"
                            .formatted(context.amount().toPlainString(), minAmount.toPlainString()));
        }

        return context.channel() == PurchaseChannel.SHOP ? shopState : onlineState;
    }
}
