package com.bank.bonus.state;

import com.bank.bonus.processor.PaymentContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.bank.bonus.state.PaymentStatesFixture.context;
import static org.assertj.core.api.Assertions.assertThat;

class BankCommissionStateTest {

    private final PaymentStatesFixture states = new PaymentStatesFixture();

    @Test
    @DisplayName("Дешёвая покупка: комиссия 10% от суммы")
    void chargesTenPercentCommission() {
        PaymentContext ctx = context("15.00", "1000.00", "0.00", "0.00");

        PaymentState next = states.commission.advance(ctx);

        assertThat(ctx.commission()).isEqualByComparingTo("1.50");
        assertThat(next).isSameAs(states.finish);
    }

    @Test
    @DisplayName("Комиссия округляется по результату: 19.99 * 10% = 1.999 → 2.00")
    void roundsResult() {
        PaymentContext ctx = context("19.99", "1000.00", "0.00", "0.00");

        states.commission.advance(ctx);

        assertThat(ctx.commission()).isEqualByComparingTo("2.00");
    }

    @Test
    @DisplayName("Комиссия не трогает бонусный счёт")
    void doesNotTouchBonus() {
        PaymentContext ctx = context("15.00", "1000.00", "0.00", "0.00");

        states.commission.advance(ctx);

        assertThat(ctx.bonus()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Комиссия прибавляется к уже начисленной, а не затирает её")
    void accumulatesWithExistingCommission() {
        PaymentContext ctx = context("10.00", "1000.00", "0.00", "0.00");
        ctx.addCommission(new BigDecimal("0.50"));

        states.commission.advance(ctx);

        assertThat(ctx.commission()).isEqualByComparingTo("1.50");
    }
}
