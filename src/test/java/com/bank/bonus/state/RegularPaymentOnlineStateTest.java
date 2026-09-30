package com.bank.bonus.state;

import com.bank.bonus.processor.PaymentContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.bank.bonus.state.PaymentStatesFixture.context;
import static org.assertj.core.api.Assertions.assertThat;

class RegularPaymentOnlineStateTest {

    private final PaymentStatesFixture states = new PaymentStatesFixture();

    @Test
    @DisplayName("Обычная покупка онлайн: 17% бонусов")
    void chargesSeventeenPercent() {
        PaymentContext ctx = context("100.00", "1000.00", "0.00", "0.00");

        states.online.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("17.00");
    }

    @Test
    @DisplayName("Округление идёт по результату: 199.99 * 17% = 33.9983 → 34.00")
    void roundsResultNotRate() {
        PaymentContext ctx = context("199.99", "1000.00", "0.00", "0.00");

        states.online.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("34.00");
    }

    @Test
    @DisplayName("Покупка 300.00 онлайн — ещё 17%, а не 30%")
    void exactlyThreeHundredStaysRegular() {
        PaymentContext ctx = context("300.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.online.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("51.00");
        assertThat(next).isSameAs(states.finish);
    }

    @Test
    @DisplayName("Дешёвая онлайн-покупка даёт бонус и уходит в комиссию")
    void cheapOnlinePurchaseGoesToCommission() {
        PaymentContext ctx = context("15.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.online.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("2.55");
        assertThat(next).isSameAs(states.commission);
    }

    @Test
    @DisplayName("Покупка 300.01 онлайн уходит в enhanced, 17% не начисляются")
    void aboveThresholdGoesEnhanced() {
        PaymentContext ctx = context("300.01", "1000.00", "0.00", "0.00");

        PaymentState next = states.online.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("0.00");
        assertThat(next).isSameAs(states.enhanced);
    }
}
