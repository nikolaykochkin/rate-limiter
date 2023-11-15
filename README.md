# Rate Limiter

Тестовое задание на позицию Java Developer

## TL;DR (JVM)

### Build

```shell
docker build -t name.nikolaikochkin/rate-limiter .
```

### Start

```shell
docker run --rm -p 8080:8080 name.nikolaikochkin/rate-limiter
```

### Test

```shell
curl http://localhost:8080/api/limit/controller -v
```

## TL;DR (GraalVM Native Application)

### Build

```shell
docker build -f Dockerfile.native -t name.nikolaikochkin/rate-limiter-native .
```

### Start

```shell
docker run --rm -p 8080:8080 name.nikolaikochkin/rate-limiter-native
```

### Test

```shell
curl http://localhost:8080/api/limit/controller -v
```

## Функциональные требования

- Написать приложение с одним методом, который возвращает HTTP 200 и пустое тело.
- Ограничивать количество запросов с одного IP адреса на этот метод в размере N штук в X минут.
- Если количество запросов больше, то должен возвращаться 502 код ошибки, до тех пор, пока количество обращений за
  заданный интервал не станет ниже N.
- Должна быть возможность настройки этих двух параметров через конфигурационный файл.
- Сделать так, чтобы это ограничение можно было применять быстро к новым методам и не только к контроллерам, а также к
  методам классов сервисного слоя.
- Написать простой JUnit-тест, который будет эмулировать работу параллельных запросов с разных IP.
- Написать простой Dockerfile для обёртки данного приложения в докер

## Нефункциональные требования

- Java 11 (или выше)
- Фреймворки: Spring + Spring Boot
- Сборка: Gradle
- Тесты: JUnit 5.x (Junit Jupiter)
- Деплой: Docker
- Реализация должна учитывать многопоточную высоконагруженную среду исполнения и потреблять как можно меньше ресурсов.
- Не использовать сторонних библиотек для тротлинга

## Выбранные технологии

- Java 17 / GraalVM
- Фреймворк: Spring Boot
- REST контроллер: WebFlux
- Унификация лимитера: Spring AOP
- Меньше кода: Lombok
- Тесты: JUnit 5, Reactor Test, Spring Boot Test
- Сборка: Gradle + graalvm.buildtools.native плагин

### Почему WebFlux?

Лучший throughput и latency "из коробки" в сравнении с Spring MVC и Quarkus

### Почему GraalVM?

- Потребления памяти: 22Mb против 150Mb
- Время старта приложения: 0.046 секунд против 0.9 под JVM

## API

| Endpoint                   | Limited    | Description                                       |
|----------------------------|------------|---------------------------------------------------|
| /api/limit/controller      | controller | ограничение на уровне контроллера для метода Mono |
| /api/limit/controller/flux | controller | ограничение на уровне контроллера для метода Flux |
| /api/limit/service         | service    | ограничение на уровне сервиса для метода Mono     |
| /api/limit/service/flux    | service    | ограничение на уровне сервиса для метода Flux     |
| /api/unlimited             | -          | метод контроллера без ограничений                 |

## Основные компоненты

### Алгоритм

- RateLimiter - интерфейс логики лимита.
- TokenBucketRateLimiter - реализация интерфейса RateLimiter с алгоритмом Token Bucket.
- RateLimiterFactory - интерфейс фабрики для создания экземпляров RateLimiter (бакетов).
- TokenBucketRateLimiterFactory - реализация фабрики RateLimiterFactory. Создает TokenBucketRateLimiter с заданными в
  properties параметрами.

### Провайдеры ключей

- RateLimitKey - общий интерфейс для ключа лимита.
- ClassMethodNameRateLimitKey - реализация, где ключ состоит из имени класса и метода на который наложено ограничение.
- RemoteHostAddressRateLimitKey - реализация, где ключом является IP адрес клиента.
- RateLimitKeyProvider - интерфейс провайдера ключа.
- ClassMethodNameKeyProvider - провайдер ключа по имени класса и метода.
- RemoteHostAddressKeyProvider - провайдер ключа по адресу клиента.
- RateLimitKeyService - сервис обеспечивает создание ключей по заданным параметрам.

### Сервис лимитов

- RateLimitService - общий интерфейс для сервиса проверки лимитов.
- InMemoryRateLimitService - реализация сервиса проверки лимитов на базе ConcurrentHashMap.

### Аспект

- RateLimitAsync - аннотация для указания асинхронных методов, к которым должны примениться лимиты.
- RateLimitAspect - аспект для обёртки асинхронных методов, помеченных аннотацией RateLimitAsync
