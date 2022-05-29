INSERT INTO genre (name)
VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

INSERT INTO rating (rating_name, description)
VALUES
('G', 'Нет возрастных ограничений'),
('PG', 'Детям рекомендуется смотреть фильм с родителями'),
('PG-13', 'Детям до 13 лет просмотр не желателен'),
('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
('NC-17', 'Лицам до 18 лет просмотр запрещён');

INSERT INTO films (name, rating_name, release_date, duration, description)
VALUES
('Круэлла', 'PG-13', '2021-05-18', 134, 'Предыстория экстравагантной и коварной Круэллы ...'),
('Король Лев', 'G', '1994-06-12', 88, 'Львенок Симба бросает вызов дяде-убийце. Величес ...');

INSERT INTO users (email, login, name, birthday)
VALUES
('nabrus@bk.ru', 'nabrus', 'rustam', '1992-12-16'),
('sveta@bk.ru', 'svetick', 'svetlana', '2001-12-16'),
('batman@bk.ru', 'batmantik', 'batman', '2005-12-16'),
('spederman@bk.ru', 'spiderman', 'tom', '2004-12-16'),
('halk@bk.ru', 'halk', 'rick', '2003-12-16'),
('superman@bk.ru', 'tom', 'tim', '2002-12-16');

INSERT INTO friendship (user_id, friend_id)
VALUES
(1,2),
(2,1),
(2,3),
(2,4),
(4,3),
(4,5),
(2,5);

INSERT INTO likes (film_id, user_id)
VALUES
(1,2),
(1,3),
(1,4),
(2,1),
(2,3),
(2,4),
(2,2);

INSERT INTO film_genre (film_id, genre_id)
VALUES
(1, 1),
(1, 2),
(1, 4),
(2, 3),
(2, 2);
