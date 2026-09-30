package com.bank.bonus.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Денежная арифметика.
 *
 * <p>Собрана в одном месте намеренно: правило округления должно быть ровно одно.
 * Если каждый state округляет по-своему, через месяц в истории появятся
 * суммы, которые не сходятся с суммой остатков, и найти расхождение будет невозможно.
 */
public final class Money {

    /** Количество знаков после запятой у всех денежных величин. */
    public static final int SCALE = 2;

    private Money() {
    }

    /** Округляет до копеек по банковскому правилу. */
    public static BigDecimal round(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Считает процент от суммы с округлением результата.
     *
     * <p>Важно: округляется именно результат, а не ставка. Для 199.99 и 17%
     * это 34.00, а не 33.99 — «полторы копейки не отдаём клиенту, не забираем у банка».
     *
     * @param base сумма-основание
     * @param rate ставка долями: 0.17 означает 17%
     */
    public static BigDecimal percentOf(BigDecimal base, BigDecimal rate) {
        return round(base.multiply(rate));
    }

    /** Ставка 100% — чтобы не плодить литералы {@code new BigDecimal("1.00")}. */
    public static BigDecimal percent(int percent) {
        return BigDecimal.valueOf(percent, 2);
    }
}
