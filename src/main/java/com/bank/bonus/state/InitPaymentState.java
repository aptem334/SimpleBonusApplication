package com.bank.bonus.state;

import com.bank.bonus.exception.InvalidAmountException;
import com.bank.bonus.exception.UnknownChannelException;
import com.bank.bonus.model.PurchaseChannel;
import com.bank.bonus.processor.PaymentContext;
import org.springframework.stereotype.Component;

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

    public InitPaymentState(RegularPaymentShopState shopState, RegularPaymentOnlineState onlineState) {
        this.shopState = shopState;
        this.onlineState = onlineState;
    }

    @Override
    public PaymentState advance(PaymentContext context) {
        if (context.channel() == null) {
            throw new UnknownChannelException("Неизвестное место покупки. Ожидается Shop или Online");
        }
        if (context.amount() == null) {
            throw new InvalidAmountException("Сумма покупки не указана");
        }
        if (context.amount().signum() <= 0) {
            throw new InvalidAmountException(
                    "Сумма покупки должна быть положительной: " + context.amount().toPlainString());
        }

        return context.channel() == PurchaseChannel.SHOP ? shopState : onlineState;
    }
}
