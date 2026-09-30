# T-009 — MockMvc вынесен из spring-boot-starter-test в Spring Boot 4

**Status:** done
**Priority:** high
**Component:** infra

## Контекст
Обнаружено при попытке написать первый интеграционный тест (T-005), 30.09.2026.

`spring-boot-starter-test` в Spring Boot 4.1.1 **не содержит** поддержки MockMvc.
Проверено деревом зависимостей и поиском по всем jar'ам в локальном репозитории:

```
$ ./mvnw dependency:tree | grep mockmvc
(пусто)
```

Модуль с `@AutoConfigureMockMvc` вынесен в отдельный стартер, и его нужно
подключать явно. Без этого любой тест с MockMvc не компилируется, а причина
неочевидна: в Boot 3 всё было в комплекте, и документация/примеры из интернета
вводят в заблуждение.

## Решение
Добавлена зависимость:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

Пакет аннотации тоже изменился:
```java
// было (Boot 3)
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

// стало (Boot 4)
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
```

## Критерии приёмки
- [x] Зависимость добавлена
- [x] Пакет аннотации исправлен
- [x] Интеграционные тесты компилируются и проходят
- [x] Причина зафиксирована в AGENTS.md

## Решение / резолюция
Сделано. Заметка перенесена в `AGENTS.md` → раздел «Тесты», чтобы следующий агент
не потратил на это 10 минут и не решил, что у него «сломанный Spring Boot».
