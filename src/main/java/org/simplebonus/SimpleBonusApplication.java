package org.simplebonus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения для обработки платежей и начисления бонусов.
 * Запускает встроенный сервер и инициализирует все компоненты приложения.
 */

@SpringBootApplication
public class SimpleBonusApplication {
    private static final Logger logger = LoggerFactory.getLogger(SimpleBonusApplication.class);
    public static void main(String[] args) {

        logger.info("Запуск приложения для обработки платежей и начисления бонусов...");
        SpringApplication.run(SimpleBonusApplication.class, args);
        logger.info("Приложение для обработки платежей и начисления бонусов успешно запущено.");
    }
}