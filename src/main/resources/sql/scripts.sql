create
database easybank;

use
easybank;

CREATE TABLE users
(
    id       INT         NOT NULL AUTO_INCREMENT,
    username VARCHAR(45) NOT NULL,
    password VARCHAR(45) NOT NULL,
    enabled  INT         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE authorities
(
    id        INT         NOT NULL AUTO_INCREMENT,
    username  VARCHAR(45) NOT NULL,
    authority VARCHAR(45) NOT NULL,
    PRIMARY KEY (ID)
);

INSERT
IGNORE INTO users VALUES(NULL, 'happy', '12345', '1');
INSERT
IGNORE INTO authorities VALUES(NULL, 'happy', 'write');


CREATE TABLE customer
(
    id    INT          NOT NULL AUTO_INCREMENT,
    email VARCHAR(45)  NOT NULL UNIQUE,
    pwd   VARCHAR(200) NOT NULL,
    role  VARCHAR(45)  NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO customer (email, pwd, role)
VALUES ('johndoe@yopmail.com', '54321', 'admin');