# Автотесты Википедии на Java

API- и UI-автотесты настоящей Википедии: REST API Wikimedia и сайт в десктопной и мобильной версии.
Пара к проекту [android-mobile-autotests](https://github.com/sonyfrid/android-mobile-autotests), где то же
приложение проверяется на Android через Appium и Maestro.

**Стек:** Java 21, Maven, JUnit 5, REST Assured, Selenide (Selenium WebDriver), AssertJ, JSON Schema, Allure,
GitHub Actions.

## Что проверяется

**API (22 теста)**, `src/test/java/.../api`

| Класс | Что внутри |
|---|---|
| `SummaryApiTest` | краткое описание статьи `/page/summary`: JSON-схема, нормализация названий (параметризованный тест), редирект USA → United States, страница неоднозначности, один элемент Wikidata у русской и английской статьи, 404, случайная статья через 303 |
| `SearchApiTest` | поиск `/search/page` и `/search/title`: нужная статья в выдаче, параметр `limit` (1, 5, 10), подсказки по названию, кириллица, пустая выдача, 400 без `q`, 400 при `limit` вне 1..100 |
| `PageApiTest` | метаданные страницы `/page/{title}/bare` по схеме и лицензии, тело ошибки 404, языковые версии статьи |

**UI (10 тестов)**, `src/test/java/.../ui`, Page Object в `ui/pages`

| Класс | Что внутри |
|---|---|
| `SearchUiTest` | точное название сразу открывает статью (en и ru), выпадающие подсказки, страница результатов, сообщение «ничего не найдено» |
| `ArticleUiTest` | оглавление и переход к разделу, инфобокс, переключение языка Earth → «Земля» |
| `MobileUiTest` | мобильная версия в эмуляции Pixel 7: заголовок, поиск, разделы, раскрытие свёрнутого раздела |

## Как устроено

- `WikiApi`: общие спецификации REST Assured для двух API (`/api/rest_v1` и `/w/rest.php/v1`), ожидаемый ответ
  `okJson()`, фильтр Allure, чтобы запросы и ответы попадали в отчёт.
- Ответы десериализуются в `record`-модели (`api/model`), структура дополнительно проверяется JSON-схемами из
  `src/test/resources/schemas`.
- `PoliteFilter`: тесты ходят в публичный сервис, поэтому между запросами есть пауза, а после ответа 429
  тест ждёт `Retry-After` и повторяет запрос один раз. Нагрузки на Википедию нет.
- UI на Selenide: умные ожидания вместо `sleep`, скриншот при падении, шаги `@Step` в Allure.
- Настройки в `config.properties`, любую можно переопределить через `-D`.

## Запуск

Нужны JDK 21, Maven и Chrome.

```bash
mvn test            # все тесты
mvn -Papi test      # только API
mvn -Pui test       # только UI
mvn -Pui test -Dselenide.headless=false   # смотреть браузер
```

Отчёт Allure:

```bash
mvn allure:serve
```

## CI

`.github/workflows/tests.yml`: API и UI идут отдельными джобами на каждый push, pull request и раз в неделю
по расписанию. Результаты Allure и скриншоты Selenide сохраняются как артефакты.
