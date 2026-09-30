package com.bank.bonus.processor;

import com.bank.bonus.model.CustomerAccount;
import com.bank.bonus.model.Money;
import com.bank.bonus.model.PurchaseChannel;

import java.math.BigDecimal;

/**
 * Изменяемое состояние платежа, которое тащат через всю цепочку состояний.
 *
 * <p>Намеренно мутабельный: состояния идут последовательно и каждое дописывает
 * свой вклад. Альтернатива — пересобирать иммутабельный объект и возвращать
 * пару (новый контекст, следующее состояние), но здесь это удваивает код состояний
 * без выигрыша: откат всё равно делает транзакция БД, а не объект.
 */
public final class PaymentContext {

    private final PurchaseChannel channel;
    private final BigDecimal amount;
    private final CustomerAccount account;

    private BigDecimal bonus = BigDecimal.ZERO.setScale(Money.SCALE);
    private BigDecimal commission = BigDecimal.ZERO.setScale(Money.SCALE);

    public PaymentContext(PurchaseChannel channel, BigDecimal amount, CustomerAccount account) {
        this.channel = channel;
        this.amount = amount;
        this.account = account;
    }

    public PurchaseChannel channel() {
        return channel;
    }

    public BigDecimal amount() {
        return amount;
    }

    public CustomerAccount account() {
        return account;
    }

    public BigDecimal bonus() {
        return bonus;
    }

    public void addBonus(BigDecimal value) {
        this.bonus = this.bonus.add(value);
    }

    public BigDecimal commission() {
        return commission;
    }

    public void addCommission(BigDecimal value) {
        this.commission = this.commission.add(value);
    }

    /** Итоговое списание с денежного счёта: сумма покупки плюс комиссия. */
    public BigDecimal totalDebit() {
        return amount.add(commission);
    }
}
