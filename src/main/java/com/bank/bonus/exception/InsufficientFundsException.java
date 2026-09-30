package com.bank.bonus.exception;

/**
 * Денег на счёте меньше, чем требует операция.
 *
 * <p>Сообщение всегда содержит обе суммы: клиенту нужно видеть не «ошибка»,
 * а насколько не хватило.
 */
public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
