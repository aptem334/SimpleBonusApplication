package org.simplebonus.exception;

public class InsufficientClientMoneyException extends AbstractSimpleBonusException {
    public InsufficientClientMoneyException(String message) {
        super("Невозможно совершить покупку: ".concat(message));
    }
}
