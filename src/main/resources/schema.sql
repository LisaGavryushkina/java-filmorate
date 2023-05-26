

DROP TABLE IF EXISTS USER_ CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS FILM_LIKE CASCADE;
DROP TABLE IF EXISTS FILM_GENRE CASCADE;
DROP TABLE IF EXISTS FRIENDSHIP CASCADE;
DROP TABLE IF EXISTS FRIENDSHIP_REQUEST CASCADE;
DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS RATING CASCADE;



CREATE TABLE IF NOT EXISTS user_
(
	user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email varchar(200) NOT NULL,
	login varchar(200) NOT NULL UNIQUE,
	name varchar(200),
	birthday date
);

CREATE TABLE IF NOT EXISTS rating
(
    rating_id integer NOT NULL PRIMARY KEY,
    name varchar(200) NOT NULL
);

INSERT INTO rating (rating_id, name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

CREATE TABLE IF NOT EXISTS genre
(
    genre_id integer NOT NULL PRIMARY KEY,
    name varchar(200) NOT NULL
);

INSERT INTO genre (genre_id, name) VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

CREATE TABLE IF NOT EXISTS film
(
    film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(200) NOT NULL,
    rating_id integer NOT NULL,
    description varchar(200),
    release_date date NOT NULL,
    duration integer NOT NULL,
    CONSTRAINT fk_film_rating_id
     FOREIGN KEY (rating_id)
            REFERENCES rating (rating_id)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id integer NOT NULL,
    genre_id integer NOT NULL,
    CONSTRAINT pk_film_genre PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genre_film_id
     FOREIGN KEY (film_id)
            REFERENCES film (film_id),
    CONSTRAINT fk_film_genre_genre_id
     FOREIGN KEY (genre_id)
            REFERENCES genre (genre_id)
);

CREATE TABLE IF NOT EXISTS film_like
(
    film_id integer NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT pk_film_like PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_like_film
     FOREIGN KEY (film_id)
            REFERENCES film (film_id),
    CONSTRAINT fk_like_user
     FOREIGN KEY (user_id)
            REFERENCES user_ (user_id)
);

CREATE TABLE IF NOT EXISTS friendship_request
(
    user_from_id integer NOT NULL,
    user_to_id integer NOT NULL,
    CONSTRAINT pk_friendship_request PRIMARY KEY (user_from_id, user_to_id),
	CONSTRAINT fk_friendship_request_user_from
     FOREIGN KEY (user_from_id)
            REFERENCES user_ (user_id),
    CONSTRAINT fk_friendship_request_user_to
     FOREIGN KEY (user_to_id)
            REFERENCES user_ (user_id)
);
