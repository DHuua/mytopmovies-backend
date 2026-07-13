# MyTopMovies — backend

Spring Boot REST API для пет-проекта MyTopMovies: категории фильмов (New, Popular,
Best for Friends, по жанрам), JWT-регистрация/логин, интеграция с TMDB.

## Стек

- Java 21, Spring Boot 3.3
- Spring Security + JWT (jjwt)
- Spring Data JPA + PostgreSQL
- Flyway (миграции схемы)
- springdoc-openapi (Swagger UI на `/docs`)

## Локальный запуск

1. Подними локальный Postgres (или используй Docker):
   ```bash
   docker run --name mtm-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=mytopmovies -p 5432:5432 -d postgres:16
   ```
2. Скопируй `.env.example` → `.env` и заполни значения (либо экспортируй переменные в shell).
3. Запусти:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Flyway накатит схему автоматически при старте.
5. Swagger UI: http://localhost:8080/docs

## Структура

```
entity/       — JPA-сущности (User, Movie, Genre, UserMovie, Friendship)
repository/   — Spring Data репозитории, включая запросы категорий
security/     — JWT-фильтр, JwtService, UserDetailsService
service/      — бизнес-логика
controller/   — REST-эндпоинты
dto/          — DTO для запросов/ответов
exception/    — глобальный обработчик ошибок
db/migration/ — Flyway SQL-миграции
```

## Эндпоинты (v1)

| Метод | Путь                         | Доступ       | Описание                          |
|-------|------------------------------|--------------|------------------------------------|
| POST  | `/api/v1/auth/register`      | публичный    | регистрация                        |
| POST  | `/api/v1/auth/login`         | публичный    | логин, возвращает JWT              |
| GET   | `/api/v1/movies/new`         | публичный    | новые фильмы                       |
| GET   | `/api/v1/movies/popular`     | публичный    | популярные фильмы                  |
| GET   | `/api/v1/movies/genre/{id}`  | публичный    | фильмы по жанру                    |
| GET   | `/api/v1/movies/for-friends` | JWT          | подборка на основе оценок друзей   |
| GET   | `/api/v1/movies/{id}`        | публичный    | карточка фильма                    |

