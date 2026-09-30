package com.bank.bonus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Запись об одной завершённой операции. ТЗ требует сохранять каждую корректно
 * завершённую операцию отдельной записью, поэтому здесь хранятся не только
 * входные данные, но и итог расчёта — иначе историю невозможно пересчитать.
 */
@Entity
@Table(name = "purchase_transaction")
public class PurchaseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private PurchaseChannel channel;

    /** Сумма покупки как её указал клиент. */
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /** Начисленный бонус. */
    @Column(name = "bonus", nullable = false, precision = 19, scale = 2)
    private BigDecimal bonus;

    /** Удержанная комиссия банка. */
    @Column(name = "commission", nullable = false, precision = 19, scale = 2)
    private BigDecimal commission;

    /** Остаток денег после операции — чтобы не пересчитывать историю. */
    @Column(name = "money_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal moneyAfter;

    @Column(name = "bonus_after", nullable = false, precision = 19, scale = 2)
    private BigDecimal bonusAfter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PurchaseTransaction() {
    }

    public PurchaseTransaction(
            TransactionType type,
            PurchaseChannel channel,
            BigDecimal amount,
            BigDecimal bonus,
            BigDecimal commission,
            BigDecimal moneyAfter,
            BigDecimal bonusAfter) {
        this.type = type;
        this.channel = channel;
        this.amount = amount;
        this.bonus = bonus;
        this.commission = commission;
        this.moneyAfter = moneyAfter;
        this.bonusAfter = bonusAfter;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public PurchaseChannel getChannel() {
        return channel;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public BigDecimal getMoneyAfter() {
        return moneyAfter;
    }

    public BigDecimal getBonusAfter() {
        return bonusAfter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
