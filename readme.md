# SimpleBonusApplication

## Описание проекта
SimpleBonusApplication — это RESTful веб-приложение на Java с использованием Spring Boot. Приложение реализует систему начисления бонусов за покупки в магазине и онлайн, а также списание комиссии для небольших покупок. Бизнес-логика построена на основе поведенческого паттерна **State**.

## Функционал
1. Оплата покупки:
  - `GET /api/payment/{Shop|Online}/{amount}` — обработка платежа.

2. Получение текущего состояния счетов:
  - `GET /api/bankAccountOfEMoney` — проверка баланса бонусного счета.
  - `GET /api/money` — проверка остатка наличных/безналичных средств.


Сервис реализует следующие операции:
- Оплата покупки
  * операция имеет два параметра как переменные URI запроса: место совершения покупки (Shop или Online)
    и сумму покупки в формате десятичной дроби;
  * при успешном выполнении операции возвращается ответ с кодом HTTP 200 без тела;
  * при некорректно заданной сумме возвращается ошибка HTTP 400 с соответствующим описанием;
  * при невозможности совершить покупку с текущим балансом возвращается ошибка HTTP 400 с соответствующим описанием.
- Возврат по запросу текущего баланса клиента.
- Возврат по запросу суммы всех полученных клиентом бонусов.
- Возврат по запросу суммы заработанной банком комиссии.

Обработка покупки (расчет комиссии, бонусов и баланса) производится в соответствии с поведенческим шаблоном "Состояние".

Каждая корректно завершенная операция сохраняется в виде отдельной записи в БД.

## Логика начисления бонусов
- Покупки в **магазине (Shop)** — 10% бонусов от суммы.
- Покупки **онлайн (Online)** — 17% бонусов от суммы.
- Покупки **меньше 20 рублей** — взимается 10% комиссии в пользу банка.
- Покупки **больше 300 рублей** — начисляется **30% бонусов**.

## Архитектура проекта
### Основные компоненты:
- **`controller`** — REST-контроллеры для обработки HTTP-запросов.
- **`service`** — бизнес-логика обработки платежей и управления бонусами.
- **`processor`** — реализация паттерна **State**, обрабатывающего платежи.
- **`state`** — отдельные классы состояний для расчета бонусов и комиссии.
- **`model`** — сущности базы данных.
- **`repository`** — DAO-слой для работы с БД.

### Реализация паттерна State
Платежи обрабатываются в нескольких состояниях:
- `InitPaymentState` — начальная обработка.
- `RegularPaymentShopState` — начисление 10% бонусов в магазине.
- `RegularPaymentOnlineState` — начисление 17% бонусов онлайн.
- `EnhancedBonusState` — начисление 30% бонусов (если покупка > 300 рублей).
- `BankCommissionState` — взимание комиссии (если покупка < 20 рублей).
- `FinishPaymentState` — финальное состояние платежа.

### Алгоритм обработки платежа
1. Контроллер принимает запрос `/api/payment/{Shop|Online}/{amount}`.
2. `PaymentProcessor` определяет начальное состояние (`InitPaymentState`).
3. В зависимости от суммы платеж переходит в соответствующее состояние.
4. Рассчитываются бонусы или комиссия.
5. Финальное состояние (`FinishPaymentState`) завершает платеж и сохраняет данные.

## Запуск приложения
1. Убедитесь, что установлен **JDK 17+** и **Maven**.
2. Склонируйте репозиторий и перейдите в папку проекта:
   ```sh
   git clone https://github.com/aptem334/SimpleBonusApplication.git
   cd SimpleBonusApplication
   ```
3. Соберите и запустите проект:
   ```sh
   mvn clean package
   java -jar target/*.jar
   ```
4. API будет доступно по адресу: `http://localhost:8080`

## Тестирование
Для тестирования используйте **Postman** или **cURL**:
```sh
curl -X GET "http://localhost:8080/api/payment/Online/100"
```