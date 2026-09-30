package com.bank.bonus.state;

import com.bank.bonus.exception.InvalidAmountException;
import com.bank.bonus.exception.UnknownChannelException;
import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.model.PurchaseChannel;
import com.bank.bonus.processor.PaymentContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InitPaymentStateTest {

    private final PaymentStatesFixture states = new PaymentStatesFixture();

    private static PaymentContext ctxWithChannel(PurchaseChannel channel, String amount) {
        return new PaymentContext(channel, new BigDecimal(amount),
                new CustomerAccount(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Канал Shop ведёт в состояние магазина")
    void shopGoesToShopState() {
        PaymentContext ctx = ctxWithChannel(PurchaseChannel.SHOP, "100.00");

        assertThat(states.init.advance(ctx)).isSameAs(states.shop);
    }

    @Test
    @DisplayName("Канал Online ведёт в состояние онлайна")
    void onlineGoesToOnlineState() {
        PaymentContext ctx = ctxWithChannel(PurchaseChannel.ONLINE, "100.00");

        assertThat(states.init.advance(ctx)).isSameAs(states.online);
    }

    @Test
    @DisplayName("Неизвестный канал отклоняется")
    void nullChannelIsRejected() {
        PaymentContext ctx = ctxWithChannel(null, "100.00");

        assertThatThrownBy(() -> states.init.advance(ctx)).isInstanceOf(UnknownChannelException.class);
    }

    @Test
    @DisplayName("Нулевая сумма отклоняется")
    void zeroIsRejected() {
        PaymentContext ctx = ctxWithChannel(PurchaseChannel.SHOP, "0");

        assertThatThrownBy(() -> states.init.advance(ctx))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("положительной");
    }

    @Test
    @DisplayName("Отрицательная сумма отклоняется")
    void negativeIsRejected() {
        PaymentContext ctx = ctxWithChannel(PurchaseChannel.SHOP, "-5.00");

        assertThatThrownBy(() -> states.init.advance(ctx))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessageContaining("-5.00");
    }

    @Test
    @DisplayName("Сумма без знаков после запятой допустима и трактуется как рубли")
    void integerAmountIsAccepted() {
        PaymentContext ctx = ctxWithChannel(PurchaseChannel.SHOP, "100");

        assertThat(states.init.advance(ctx)).isSameAs(states.shop);
    }
}
