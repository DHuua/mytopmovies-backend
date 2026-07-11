CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    avatar_url      VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE movies (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tmdb_id         BIGINT NOT NULL UNIQUE,
    title           VARCHAR(500) NOT NULL,
    overview        TEXT,
    poster_path     VARCHAR(500),
    release_date    DATE,
    popularity      DOUBLE PRECISION DEFAULT 0,
    vote_average    DOUBLE PRECISION DEFAULT 0,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_movies_popularity ON movies (popularity DESC);
CREATE INDEX idx_movies_release_date ON movies (release_date DESC);

CREATE TABLE genres (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tmdb_genre_id   BIGINT NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE movie_genres (
    movie_id        UUID NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    genre_id        UUID NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (movie_id, genre_id)
);

CREATE TABLE user_movies (
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    movie_id        UUID NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    rating          SMALLINT CHECK (rating BETWEEN 1 AND 10),
    is_favorite     BOOLEAN NOT NULL DEFAULT false,
    is_watched      BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, movie_id)
);

CREATE TABLE friendships (
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    friend_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, ACCEPTED, BLOCKED
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, friend_id),
    CHECK (user_id <> friend_id)
);
