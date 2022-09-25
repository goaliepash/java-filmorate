DROP TABLE IF EXISTS friendship;
DROP TABLE IF EXISTS likes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS films;
DROP TABLE IF EXISTS mpa;

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(200),
    login    VARCHAR(200),
    name     VARCHAR(200),
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship
(
    user_id   INTEGER REFERENCES users (id),
    friend_id INTEGER REFERENCES users (id),
    status    VARCHAR(12) DEFAULT 'UNCONFIRMED'
);

CREATE TABLE IF NOT EXISTS mpa
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL
);

INSERT INTO mpa (name)
VALUES ('G');
INSERT INTO mpa (name)
VALUES ('PG');
INSERT INTO mpa (name)
VALUES ('PG-13');
INSERT INTO mpa (name)
VALUES ('R');
INSERT INTO mpa (name)
VALUES ('NC-17');

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(200) NOT NULL
);

INSERT INTO genres (name)
VALUES ('Комедия');
INSERT INTO genres (name)
VALUES ('Драма');
INSERT INTO genres (name)
VALUES ('Мультфильм');
INSERT INTO genres (name)
VALUES ('Триллер');
INSERT INTO genres (name)
VALUES ('Документальный');
INSERT INTO genres (name)
VALUES ('Боевик');

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(200),
    description  VARCHAR(200),
    release_date DATE    NOT NULL,
    duration     INTEGER,
    rate         INTEGER,
    mpa_id       INTEGER NOT NULL REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  INTEGER REFERENCES films (id),
    genre_id INTEGER REFERENCES genres (id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INTEGER REFERENCES films (id),
    user_id INTEGER REFERENCES users (id)
);