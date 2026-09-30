package com.bank.bonus.exception;

/** Сумма покупки задана некорректно: не число, ноль или отрицательное. */
public class InvalidAmountException extends BusinessException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
