package com.bank.bonus.state;

import com.bank.bonus.exception.InsufficientFundsException;
import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.model.Money;
import com.bank.bonus.model.PurchaseChannel;
import com.bank.bonus.model.PurchaseTransaction;
import com.bank.bonus.processor.PaymentContext;
import com.bank.bonus.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class FinishPaymentStateTest {

    private final List<PurchaseTransaction> saved = new ArrayList<>();
    private final TransactionRepository repository = mock(TransactionRepository.class);

    private FinishPaymentState state;

    @BeforeEach
    void setUp() {
        saved.clear();
        doAnswer(invocation -> {
            PurchaseTransaction transaction = invocation.getArgument(0);
            saved.add(transaction);
            return transaction;
        }).when(repository).save(any(PurchaseTransaction.class));
        state = new FinishPaymentState(repository);
    }

    @Test
    @DisplayName("Списывает сумму покупки и начисляет бонус")
    void appliesPurchaseAndBonus() {
        CustomerAccount account = new CustomerAccount(new BigDecimal("1000.00"), BigDecimal.ZERO, BigDecimal.ZERO);
        PaymentContext ctx = new PaymentContext(PurchaseChannel.SHOP, new BigDecimal("100.00"), account);
        ctx.addBonus(new BigDecimal("10.00"));

        PaymentState next = state.advance(ctx);

        assertThat(next).isNull();
        assertThat(account.getMoney()).isEqualByComparingTo("900.00");
        assertThat(account.getBonus()).isEqualByComparingTo("10.00");
        assertThat(saved).hasSize(1);
    }

    @Test
    @DisplayName("Комиссия удерживается из денег сверх суммы покупки")
    void commissionIsDebitedOnTopOfAmount() {
        CustomerAccount account = new CustomerAccount(new BigDecimal("100.00"), BigDecimal.ZERO, BigDecimal.ZERO);
        PaymentContext ctx = new PaymentContext(PurchaseChannel.SHOP, new BigDecimal("15.00"), account);
        ctx.addBonus(new BigDecimal("1.50"));
        ctx.addCommission(new BigDecimal("1.50"));

        state.advance(ctx);

        assertThat(account.getMoney()).isEqualByComparingTo("83.50");
        assertThat(account.getBankCommission()).isEqualByComparingTo("1.50");
    }

    @Test
    @DisplayName("Нехватка средств учитывает и комиссию: 20.00 на счёте не покрывают покупку 19.00 с комиссией")
    void balanceCheckIncludesCommission() {
        CustomerAccount account = new CustomerAccount(new BigDecimal("20.00"), BigDecimal.ZERO, BigDecimal.ZERO);
        PaymentContext ctx = new PaymentContext(PurchaseChannel.SHOP, new BigDecimal("19.00"), account);
        ctx.addCommission(new BigDecimal("1.90"));

        assertThatThrownBy(() -> state.advance(ctx))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("20.90")
                .hasMessageContaining("20.00");
    }

    @Test
    @DisplayName("Недостаток средств не меняет счёт и не пишет транзакцию")
    void insufficientFundsLeavesNoTrace() {
        CustomerAccount account = new CustomerAccount(new BigDecimal("10.00"), BigDecimal.ZERO, BigDecimal.ZERO);
        PaymentContext ctx = new PaymentContext(PurchaseChannel.SHOP, new BigDecimal("50.00"), account);

        assertThatThrownBy(() -> state.advance(ctx)).isInstanceOf(InsufficientFundsException.class);

        assertThat(account.getMoney()).isEqualByComparingTo("10.00");
        assertThat(saved).isEmpty();
    }

    @Test
    @DisplayName("В транзакцию пишется итог расчёта, а не только входные данные")
    void transactionKeepsCalculatedResult() {
        CustomerAccount account = new CustomerAccount(new BigDecimal("500.00"), BigDecimal.ZERO, BigDecimal.ZERO);
        PaymentContext ctx = new PaymentContext(PurchaseChannel.ONLINE, new BigDecimal("200.00"), account);
        ctx.addBonus(Money.percentOf(new BigDecimal("200.00"), Money.percent(17)));

        state.advance(ctx);

        PurchaseTransaction transaction = saved.getFirst();
        assertThat(transaction.getBonus()).isEqualByComparingTo("34.00");
        assertThat(transaction.getMoneyAfter()).isEqualByComparingTo("300.00");
        assertThat(transaction.getBonusAfter()).isEqualByComparingTo("34.00");
        assertThat(transaction.getChannel()).isEqualTo(PurchaseChannel.ONLINE);
    }
}
