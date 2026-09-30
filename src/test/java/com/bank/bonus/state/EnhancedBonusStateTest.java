package com.bank.bonus.state;

import com.bank.bonus.processor.PaymentContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.bank.bonus.state.PaymentStatesFixture.context;
import static org.assertj.core.api.Assertions.assertThat;

class EnhancedBonusStateTest {

    private final PaymentStatesFixture states = new PaymentStatesFixture();

    @Test
    @DisplayName("Покупка дороже 300: 30% бонусов и переход к финалу")
    void chargesThirtyPercent() {
        PaymentContext ctx = context("400.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.enhanced.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("120.00");
        assertThat(next).isSameAs(states.finish);
    }

    @Test
    @DisplayName("Канал покупки на enhanced-тариф не влияет (решение T-002)")
    void ignoresChannel() {
        PaymentContext online = context("400.00", "1000.00", "0.00", "0.00");
        PaymentContext shop = context("400.00", "1000.00", "0.00", "0.00");

        states.enhanced.advance(online);
        states.enhanced.advance(shop);

        assertThat(online.bonus()).isEqualByComparingTo(shop.bonus());
        assertThat(online.bonus()).isEqualByComparingTo("120.00");
    }

    @Test
    @DisplayName("Граница 300.01: 30% от 300.01 = 90.003 → 90.00")
    void roundsAtBoundary() {
        PaymentContext ctx = context("300.01", "1000.00", "0.00", "0.00");

        states.enhanced.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("90.00");
    }

    @Test
    @DisplayName("Enhanced не начисляет комиссию: диапазоны «<20» и «>300» не пересекаются")
    void neverChargesCommission() {
        PaymentContext ctx = context("400.00", "1000.00", "0.00", "0.00");

        states.enhanced.advance(ctx);

        assertThat(ctx.commission()).isEqualByComparingTo("0.00");
    }
}
