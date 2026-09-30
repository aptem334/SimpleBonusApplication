# T-004 — PaymentProcessor и транзакционный сервис

**Status:** todo
**Priority:** high
**Component:** processor

## Контекст
Состояния готовы, но никто их не прогоняет. Нужен `PaymentProcessor`, который
начинает с `InitPaymentState` и идёт по цепочке до `FinishPaymentState`,
и сервис-обёртка с транзакцией.

## Требование
`PaymentProcessor.process(channel, amount)` прогоняет платёж и сохраняет транзакцию.
При недостатке средств — исключение, состояние счёта не меняется (откат транзакции).

## Критерии приёмки
- [ ] `PaymentProcessor` начинает с `InitPaymentState` и доводит до `FinishPaymentState`
- [ ] Вся операция обёрнута в `@Transactional`
- [ ] Недостаток средств → исключение, счёт не изменился
- [ ] Каждая успешная операция создаёт запись в `transactions`
- [ ] Тест: неудачная покупка не оставляет следов в БД
- [ ] `./mvnw clean verify` зелёный

## Решение / резолюция
_ждёт выполнения_
