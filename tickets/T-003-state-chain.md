# T-003 — Цепочка состояний платежа (State pattern)

**Status:** todo
**Priority:** high
**Component:** state

## Контекст
Ядро задачи. Шесть состояний, каждое выполняет одно правило и возвращает следующее.
Диапазоны `< 20` и `> 300` не пересекаются, поэтому EnhancedBonus и BankCommission
не могут сработать одновременно — это упрощает граф переходов.

## Требование
Реализованы `InitPaymentState`, `RegularPaymentShopState`, `RegularPaymentOnlineState`,
`EnhancedBonusState`, `BankCommissionState`, `FinishPaymentState` — все реализуют `PaymentState`.
Каждое состояние мутирует контекст платежа и возвращает следующее состояние.

## Критерии приёмки
- [ ] Интерфейс `PaymentState` с одним методом перехода
- [ ] Каждое состояние — отдельный класс в `state/`
- [ ] `InitPaymentState` валидирует канал и сумму
- [ ] `RegularPaymentShopState` начисляет 10%, `RegularPaymentOnlineState` — 17%
- [ ] `EnhancedBonusState` начисляет 30% (канал не учитывается)
- [ ] `BankCommissionState` списывает 10% в `bankCommission`
- [ ] Тесты на каждый переход, включая граничные значения 20.00 / 300.00 / 300.01
- [ ] `./mvnw clean verify` зелёный

## Решение / резолюция
_ждёт выполнения_
