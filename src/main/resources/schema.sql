CREATE TABLE IF NOT EXISTS users (
    user_id int   NOT NULL AUTO_INCREMENT,
    email varchar(255)   NOT NULL UNIQUE,
    login varchar(255)   NOT NULL UNIQUE,
    name varchar(255)   NOT NULL,
    birthday date   NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (
        user_id
     )
);

CREATE TABLE IF NOT EXISTS films (
    film_id int   NOT NULL AUTO_INCREMENT,
    name varchar(255)   NOT NULL,
    rating_id int   NOT NULL,
    release_date date   NOT NULL,
    duration int   NOT NULL,
    description varchar(200)   NOT NULL,
    CONSTRAINT pk_films PRIMARY KEY (
        film_id
     )
);

CREATE TABLE IF NOT EXISTS rating (
    rating_id int   NOT NULL,
    rating_name varchar(255)   NOT NULL,
    CONSTRAINT pk_rating PRIMARY KEY (
        rating_id
     )
);

CREATE TABLE IF NOT EXISTS likes (
    film_id int   NOT NULL,
    user_id int   NOT NULL,
    CONSTRAINT pk_likes PRIMARY KEY (
        film_id,user_id
     )
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id int   NOT NULL,
    friend_id int   NOT NULL,
    accept bool  NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_friendship PRIMARY KEY (
        user_id,friend_id
     )
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id int   NOT NULL,
    genre_id int   NOT NULL,
    CONSTRAINT pk_film_genre PRIMARY KEY (
        film_id,genre_id
     )
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id int   NOT NULL,
    name varchar(255)   NOT NULL,
    CONSTRAINT pk_genre PRIMARY KEY (
        genre_id
     )
);

ALTER TABLE films ADD CONSTRAINT IF NOT EXISTS fk_films_rating_id FOREIGN KEY(rating_id)
REFERENCES rating (rating_id);

ALTER TABLE likes ADD CONSTRAINT IF NOT EXISTS fk_likes_film_id FOREIGN KEY(film_id)
REFERENCES films (film_id);

ALTER TABLE likes ADD CONSTRAINT IF NOT EXISTS fk_likes_user_id FOREIGN KEY(user_id)
REFERENCES users (user_id);

ALTER TABLE friendship ADD CONSTRAINT IF NOT EXISTS fk_friendship_user_id FOREIGN KEY(user_id)
REFERENCES users (user_id);

ALTER TABLE friendship ADD CONSTRAINT IF NOT EXISTS fk_friendship_friend_id FOREIGN KEY(friend_id)
REFERENCES users (user_id);

ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS fk_film_genre_film_id FOREIGN KEY(film_id)
REFERENCES films (film_id);

ALTER TABLE film_genre ADD CONSTRAINT IF NOT EXISTS fk_film_genre_genre_id FOREIGN KEY(genre_id)
REFERENCES genre (genre_id);
