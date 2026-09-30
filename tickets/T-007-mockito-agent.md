# T-007 — Mockito цепляется к JVM динамически

**Status:** done
**Priority:** medium
**Component:** infra

## Контекст
Обнаружено при первом запуске `./mvnw clean verify` (30.09.2026):

```
Mockito is currently self-attaching to enable the inline-mock-maker.
This will no longer work in future releases of the JDK.
Please add Mockito as an agent to your build
```

Mockito подгружает java-agent в рантайме. На Java 25 это пока работает,
но с JDK 26+ динамическая загрузка агентов будет запрещена по умолчанию
(`-XX:-EnableDynamicAgentLoading`), и **все тесты с моками молча сломаются**
при смене JDK — причём не сразу, а в момент апгрейда, когда причина неочевидна.

Раньше это можно было отложить: сейчас в проекте моков нет.
Но T-005 добавит `@MockitoBean` для проверки маппинга ошибок, то есть
проблема станет нашей раньше, чем мы до неё дойдём.

## Требование
Mockito подключается как статический java-agent на этапе surefire, а не сам себя
цепляет в рантайме.

## Критерии приёмки
- [ ] В `pom.xml` добавлена конфигурация `-javaagent` для surefire
- [ ] Предупреждение `self-attaching` пропало из вывода `./mvnw clean verify`
- [ ] Тесты с моками (после T-005) проходят
- [ ] `./mvnw clean verify` зелёный

## Решение / резолюция
Сделано. В `pom.xml` в настройках `maven-surefire-plugin` добавлен:

```xml
<argLine>-javaagent:${settings.localRepository}/org/mockito/mockito-core/${mockito.version}/mockito-core-${mockito.version}.jar</argLine>
```

Путь собирается из свойств, которые Spring Boot parent и так подставляет
(`${mockito.version}` оттуда), поэтому обновление Mockito не сломает путь вручную.

Проверено: предупреждения `Mockito is currently self-attaching` и
`WARNING: A Java agent has been loaded dynamically` в выводе `./mvnw clean verify`
больше нет, 64 теста проходят.
