# bonus-service

Сервис начисления бонусов и комиссий за покупки. REST API на Spring Boot 4 / Java 25,
бизнес-логика — поведенческий паттерн **State**.

Проект заодно служит учебным стендом для работы с coding-агентами:
правила зафиксированы в [`AGENTS.md`](AGENTS.md) и [`docs/DOMAIN.md`](docs/DOMAIN.md),
работа ведётся через тикеты в [`tickets/`](tickets/).

---

## Запуск

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 25)

./mvnw spring-boot:run      # http://localhost:8080
./mvnw clean verify         # сборка + все тесты
```

База — H2 в памяти, схема создаётся сама. Стартовый баланс: `money = 100000.00`.

### Примеры

```bash
curl localhost:8080/api/payment/Shop/100.00     # 200, пустое тело
curl localhost:8080/api/money                   # 99900.00
curl localhost:8080/api/bankAccountOfEMoney     # 10.00
curl localhost:8080/api/commission              # 0.00

curl -i localhost:8080/api/payment/Shop/abc     # 400 Некорректный формат суммы: 'abc'
curl -i localhost:8080/api/payment/Market/100   # 400 Неизвестное место покупки: 'Market'...
curl -i localhost:8080/api/payment/Shop/999999  # 400 Недостаточно средств: нужно 999999.00...
```

### Правила

| Условие | Результат |
|---|---|
| `Shop` | +10% бонусов |
| `Online` | +17% бонусов |
| `amount > 300` | +30% бонусов **вместо** 10/17 |
| `amount < 20` | −10% комиссия банку |
| не хватает средств | HTTP 400, платёж не проходит |

Полное описание с таблицей переходов и сценарными примерами — [`docs/DOMAIN.md`](docs/DOMAIN.md).

---

## Структура

```
controller/    HTTP: разбор строк запроса, вызовы сервиса
service/       PaymentService — граница транзакции, загрузка счёта
processor/     PaymentProcessor — прогон платежа по цепочке состояний
state/         Шесть состояний, каждое = одно правило
model/         JPA-сущности, enum'ы, Money (округление)
repository/    Spring Data JPA
exception/     Доменные ошибки
```

Поток платежа:

```
InitPaymentState → RegularPaymentShopState ─┐
                  RegularPaymentOnlineState ─┤
                                             ├─ (>300) → EnhancedBonusState ──┐
                                             └─ (<20)  → BankCommissionState ─┤
                                                                        FinishPaymentState
```

---

## Как вести работу с агентом

Читать [`AGENTS.md`](AGENTS.md) — это контракт: команды, соглашения, формат тикетов
и цикл работы. Кратко цикл выглядит так:

```
взять тикет → реализовать → написать тест → ./mvnw clean verify
   → красное? разобраться: дефект / новое требование / неоднозначность
   → закрыть тикет
```

### Статусы тикетов

| Тикет | Статус | Суть |
|---|---|---|
| T-001 | done | Инфраструктура: сущности, репозитории, конфиг |
| T-002 | question | Бонус >300 заменяет обычный или суммируется → **решено: заменяет** |
| T-003 | done | Цепочка состояний (State pattern) |
| T-004 | done | PaymentProcessor и транзакционный сервис |
| T-005 | done | REST API и маппинг ошибок |
| T-006 | done | Интеграционные тесты, сценарная таблица |
| T-007 | done | Mockito как статический java-agent |
| T-008 | **question** | Сумма с 3+ знаками молча округляется вверх |
| T-009 | done | MockMvc вынесен из starter-test в Spring Boot 4 |

Открытых вопросов к заказчику — один: **T-008**.

---

## Тесты

64 теста: 24 юнит-теста состояний + 40 интеграционных через MockMvc
(сценарная таблица из 13 строк, границы ошибок, накопление бонусов).

Проверить, что тесты реально что-то ловят:

```bash
# сломать правило и убедиться, что сборка стала красной
sed -i.bak 's/Money.percent(17)/Money.percent(10)/' \
  src/main/java/com/bank/bonus/state/RegularPaymentOnlineState.java
./mvnw clean verify     # ожидаем FAILURE
mv src/main/java/com/bank/bonus/state/RegularPaymentOnlineState.java.bak \
   src/main/java/com/bank/bonus/state/RegularPaymentOnlineState.java
```
