DROP TABLE if exists people;

CREATE TABLE people  (
    person_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);
DROP TABLE if exists hospital;

CREATE TABLE hospital  (
    rnum INT PRIMARY KEY,
    duty_name VARCHAR(300)
);