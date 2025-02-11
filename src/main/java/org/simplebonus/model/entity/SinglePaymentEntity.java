package org.simplebonus.model.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * Сущность платежа.
 */
@Entity
@NoArgsConstructor
@Table(name = "payment_history")
@Data
@AllArgsConstructor
@Builder
@JsonAutoDetect
public class SinglePaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "place", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentPlace place;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "client_money_before", nullable = false)
    private BigDecimal clientMoneyBefore;

    @Column(name = "client_bonus")
    private BigDecimal clientBonus;

    @Column(name = "bank_commission")
    private BigDecimal bankCommission;

    @Column(name = "client_money_after", nullable = false)
    private BigDecimal clientMoneyAfter;

}
