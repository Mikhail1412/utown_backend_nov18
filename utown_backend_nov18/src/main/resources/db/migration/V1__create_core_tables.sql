CREATE TABLE restaurants (
                             id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name         VARCHAR(255) NOT NULL,
                             address      VARCHAR(512) NOT NULL,
                             phone_number VARCHAR(50),
                             description  TEXT
);
