package org.simplebonus.exception;

public class WrongInputDataFormatException extends AbstractSimpleBonusException {
    public WrongInputDataFormatException(String message) {
        super("Не удалось привести к числовому формату: ".concat(message));
    }
}
