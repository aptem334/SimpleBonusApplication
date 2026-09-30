package com.bank.bonus.exception;

/**
 * Базовая ошибка бизнес-правил.
 *
 * <p>Намеренно {@link RuntimeException}, а не проверяемое исключение: все эти
 * ситуации — ожидаемый исход запроса, а не сбой. Ловится централизованно в
 * {@code RestExceptionHandler} и превращается в HTTP 400.
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }
}
