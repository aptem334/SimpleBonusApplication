# T-001 — Инфраструктура: конфиг, сущности, репозитории

**Status:** done
**Priority:** high
**Component:** model

## Контекст
Доменные правила описаны в `docs/DOMAIN.md`, но хранилища нет. Нужен фундамент:
JPA-сущности счёта и транзакции, репозитории, конфигурация стартовых балансов и H2.
Без этого нельзя написать ни одного содержательного теста.

## Требование
При старте приложения в БД есть одна запись счёта клиента.
Балансы `money`, `bonus`, `bankCommission` берутся из `application.yml`, а не хардкодятся.

## Критерии приёмки
- [ ] `CustomerAccount` — сущность с тремя `BigDecimal`-полями
- [ ] `Transaction` — сущность записи об операции (тип, сумма, бонус, комиссия, канал, время)
- [ ] `AccountRepository` и `TransactionRepository` — Spring Data JPA
- [ ] `app.initial.money`, `app.initial.bonus`, `app.initial.bank-commission` в `application.yml`
- [ ] Тест: счёт создаётся при старте с корректными значениями
- [ ] `./mvnw clean verify` зелёный

## Решение / резолюция
Сделано: `PurchaseChannel` (enum + разбор из URL), `TransactionType`, `CustomerAccount`,
`PurchaseTransaction`, `AccountRepository`, `TransactionRepository`,
`InitialBalanceProperties` (record, `@ConfigurationProperties`), `AccountInitializer`
(идемпотентный `ApplicationRunner`), `application.yml` с H2 и `create-drop`.

В основном классе добавлена `@ConfigurationPropertiesScan` — без неё record
с `@ConfigurationProperties` не регистрируется и поля молча остаются `null`.

Тесты: `AccountInitializerTest` (2), `PurchaseChannelTest` (10) — все зелёные,
`./mvnw clean verify` → BUILD SUCCESS.

Побочная находка вынесена в T-007 (Mockito self-attaching на Java 25).
