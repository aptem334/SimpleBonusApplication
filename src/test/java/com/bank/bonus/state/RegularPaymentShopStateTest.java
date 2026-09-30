package com.bank.bonus.state;

import com.bank.bonus.processor.PaymentContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.bank.bonus.state.PaymentStatesFixture.context;
import static org.assertj.core.api.Assertions.assertThat;

class RegularPaymentShopStateTest {

    private final PaymentStatesFixture states = new PaymentStatesFixture();

    @Test
    @DisplayName("Обычная покупка в магазине: 10% бонусов и переход сразу к финалу")
    void chargesTenPercent() {
        PaymentContext ctx = context("100.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.shop.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("10.00");
        assertThat(next).isSameAs(states.finish);
    }

    @Test
    @DisplayName("Покупка ровно 20.00 — порог не пробит, комиссии нет")
    void exactlyTwentyHasNoCommission() {
        PaymentContext ctx = context("20.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.shop.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("2.00");
        assertThat(ctx.commission()).isEqualByComparingTo("0.00");
        assertThat(next).isSameAs(states.finish);
    }

    @Test
    @DisplayName("Покупка ровно 300.00 — ещё обычный тариф магазина, а не enhanced")
    void exactlyThreeHundredStaysRegular() {
        PaymentContext ctx = context("300.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.shop.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("30.00");
        assertThat(next).isSameAs(states.finish);
    }

    @Test
    @DisplayName("Покупка 300.01 уходит в enhanced, обычные 10% не начисляются")
    void justAboveThresholdGoesEnhanced() {
        PaymentContext ctx = context("300.01", "1000.00", "0.00", "0.00");

        PaymentState next = states.shop.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("0.00");
        assertThat(next).isSameAs(states.enhanced);
    }

    @Test
    @DisplayName("Дешёвая покупка в магазине: бонус начисляется, платёж уходит в комиссию")
    void cheapPurchaseGoesToCommission() {
        PaymentContext ctx = context("15.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.shop.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("1.50");
        assertThat(next).isSameAs(states.commission);
    }
}
