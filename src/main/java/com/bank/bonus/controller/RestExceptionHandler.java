package com.bank.bonus.controller;

import com.bank.bonus.exception.AccountNotFoundException;
import com.bank.bonus.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Превращает доменные ошибки в HTTP-ответы.
 *
 * <p>Без этого класса клиент получил бы 500 со стектрейсом на каждый неверный ввод —
 * и по стектрейсу клиент, а не разработчик, должен был бы догадываться, что не так.
 */
@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    /** Нарушение бизнес-правила: некорректный ввод, нехватка средств. Клиент прав — 400. */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException e) {
        log.info("Запрос отклонён: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    /** Счёт не инициализирован: запрос корректен, сломались мы. Это 500. */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException e) {
        log.error("Внутренняя ошибка: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
