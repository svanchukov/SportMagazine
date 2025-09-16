CREATE TABLE "user" (
                      id BIGINT PRIMARY KEY,
                      name VARCHAR(128) NOT NULL,
                      email VARCHAR(64) NOT NULL UNIQUE,
                      phone_number VARCHAR(64) NOT NULL UNIQUE
);


ALTER TABLE "user" ADD COLUMN version BIGINT;


