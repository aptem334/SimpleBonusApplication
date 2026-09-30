package com.bank.bonus.state;

import com.bank.bonus.processor.PaymentContext;

/**
 * Состояние платежа — одно правило за состояние.
 *
 * <p>Конструкторы конкретных состояний принимают типы-коллабораторы
 * ({@code EnhancedBonusState}, а не {@code PaymentState}) намеренно:
 * Spring внедряет их по типу однозначно, а граф переходов виден в коде буквально.
 * Плата за это — в юнит-тестах граф собирается руками в {@code PaymentStatesFixture},
 * зато переходы проверяются на настоящих состояниях, а не на заглушках.
 */
@FunctionalInterface
public interface PaymentState {

    /**
     * Применяет правило этого состояния к контексту.
     *
     * @return следующее состояние либо {@code null}, если цепочка завершена
     */
    PaymentState advance(PaymentContext context);
}
