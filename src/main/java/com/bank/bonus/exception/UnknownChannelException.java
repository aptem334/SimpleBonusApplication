package com.bank.bonus.exception;

/** Место покупки не распознано: в URL пришло что-то, кроме {@code Shop} и {@code Online}. */
public class UnknownChannelException extends BusinessException {

    public UnknownChannelException(String message) {
        super(message);
    }
}
