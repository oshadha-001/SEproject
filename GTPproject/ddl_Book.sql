CREATE TABLE book
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    title    VARCHAR(255)          NULL,
    author   VARCHAR(255)          NULL,
    price    DOUBLE                NOT NULL,
    quantity INT                   NOT NULL,
    CONSTRAINT pk_book PRIMARY KEY (id)
);