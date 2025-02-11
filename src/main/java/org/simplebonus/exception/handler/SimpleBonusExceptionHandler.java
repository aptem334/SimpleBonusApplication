package org.simplebonus.exception.handler;

import org.simplebonus.exception.AbstractSimpleBonusException;
import org.simplebonus.exception.InsufficientClientMoneyException;
import org.simplebonus.exception.WrongInputDataFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * / Обработчик исключений.
 */
@RestControllerAdvice
public class SimpleBonusExceptionHandler {
    @ExceptionHandler({InsufficientClientMoneyException.class,
            WrongInputDataFormatException.class})
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleInsufficientMoneyException(AbstractSimpleBonusException exception) {
        ErrorResponse errorResponse = new ErrorResponse("Некорректный запрос", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
