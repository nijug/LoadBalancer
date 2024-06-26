DELIMITER $$

CREATE DATABASE IF NOT EXISTS mydatabase;
USE mydatabase;

CREATE TABLE IF NOT EXISTS books (
                                     id INT PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    year_published INT NOT NULL
    );

DROP PROCEDURE IF EXISTS InsertBooksData;

CREATE PROCEDURE InsertBooksData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE genre VARCHAR(255);
    DECLARE title VARCHAR(255);
    DECLARE author VARCHAR(255);
    DECLARE year_published INT;

    WHILE i <= 100 DO
        SET genre = CASE i MOD 7
            WHEN 1 THEN 'Fiction'
            WHEN 2 THEN 'Non-Fiction'
            WHEN 3 THEN 'Science Fiction'
            WHEN 4 THEN 'Fantasy'
            WHEN 5 THEN 'Mystery'
            WHEN 6 THEN 'Thriller'
            ELSE 'Romance'
END;

        SET title = CONCAT('Book Title ', i);
        SET author = CONCAT('Author ', i);
        SET year_published = 1950 + (i MOD 71);

INSERT INTO books (id, title, author, genre, year_published) VALUES (i, title, author, genre, year_published);

SET i = i + 1;
END WHILE;
END $$

CALL InsertBooksData();
