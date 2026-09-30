package com.bank.bonus.exception;

/**
 * Счёт клиента не найден — приложение стартовало, но инициализация не отработала.
 *
 * <p>Намеренно НЕ наследуется от {@link BusinessException}: клиент в этом не виноват,
 * запрос был корректным. Поэтому наружу уходит 500, а не 400 — иначе мы бы
 * перекладывали свою поломку на пользователя.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
