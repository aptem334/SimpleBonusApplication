package com.bank.bonus.processor;

import com.bank.bonus.state.InitPaymentState;
import com.bank.bonus.state.PaymentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Прогоняет платёж по цепочке состояний.
 *
 * <p>Знает только две вещи: с какого состояния стартовать и когда остановиться.
 * Всё остальное — в самих состояниях. Добавить новый тариф начисления не требует
 * правок здесь.
 */
@Component
public class PaymentProcessor {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    /**
     * Страховка от бесконечного цикла. Сейчас максимальная длина цепочки — 4 перехода
     * (Init → Regular → Commission → Finish), так что 10 — с большим запасом.
     * Если кто-то когда-нибудь сделает состояние, возвращающее само себя,
     * мы получим понятную ошибку вместо зависшего потока и переполнения стека.
     */
    private static final int MAX_TRANSITIONS = 10;

    private final InitPaymentState initPaymentState;

    public PaymentProcessor(InitPaymentState initPaymentState) {
        this.initPaymentState = initPaymentState;
    }

    /** Прогоняет платёж до конца цепочки. Счёт и запись в БД меняют состояния. */
    public void process(PaymentContext context) {
        PaymentState state = initPaymentState;
        int transitions = 0;

        while (state != null) {
            if (++transitions > MAX_TRANSITIONS) {
                throw new IllegalStateException(
                        "Цепочка состояний не завершилась за " + MAX_TRANSITIONS + " переходов — вероятно, есть цикл");
            }
            log.debug("Переход в состояние {}", state.getClass().getSimpleName());
            state = state.advance(context);
        }

        log.debug("Платёж обработан за {} переходов", transitions);
    }
}
