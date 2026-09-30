package com.bank.bonus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Счёт клиента. В системе он один (спецификация не предусматривает авторизации
 * и нескольких клиентов), поэтому у таблицы одна строка — но id остаётся,
 * чтобы не пришлось переделывать схему, когда клиентов станет много.
 */
@Entity
@Table(name = "customer_account")
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Наличные и безналичные средства. Списывается при покупке и при комиссии. */
    @Column(name = "money", nullable = false, precision = 19, scale = 2)
    private BigDecimal money;

    /** Бонусный счёт (e-money). Только растёт — списывать бонусы пока не требуется. */
    @Column(name = "bonus", nullable = false, precision = 19, scale = 2)
    private BigDecimal bonus;

    /** Накопленная комиссия в пользу банка. */
    @Column(name = "bank_commission", nullable = false, precision = 19, scale = 2)
    private BigDecimal bankCommission;

    /** JPA требует защитного конструктора. */
    protected CustomerAccount() {
    }

    public CustomerAccount(BigDecimal money, BigDecimal bonus, BigDecimal bankCommission) {
        this.money = money;
        this.bonus = bonus;
        this.bankCommission = bankCommission;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public BigDecimal getBankCommission() {
        return bankCommission;
    }

    public void setBankCommission(BigDecimal bankCommission) {
        this.bankCommission = bankCommission;
    }
}
