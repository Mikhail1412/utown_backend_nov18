CREATE TABLE roles (
                       id   BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
                       id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name  VARCHAR(255) NOT NULL,
                       name       VARCHAR(512),
                       age        INT,
                       email      VARCHAR(255) NOT NULL UNIQUE,
                       password   VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id) REFERENCES users (id)
                                    ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id) REFERENCES roles (id)
                                    ON DELETE CASCADE
);

CREATE TABLE restaurants (
                             id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name         VARCHAR(255) NOT NULL,
                             address      VARCHAR(512) NOT NULL,
                             phone_number VARCHAR(50),
                             description  TEXT,
                             status       VARCHAR(20) NOT NULL DEFAULT 'OPEN'

);

CREATE TABLE dining_areas (
                              id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name         VARCHAR(255) NOT NULL,
                              description  TEXT,
                              restaurant_id BIGINT NOT NULL,
                              CONSTRAINT fk_dining_area_restaurant
                                  FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
                                      ON DELETE CASCADE
);

CREATE TABLE restaurant_tables (
                                   id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   table_number  VARCHAR(50) NOT NULL,
                                   capacity      INT NOT NULL,
                                   active        BOOLEAN NOT NULL DEFAULT TRUE,
                                   dining_area_id BIGINT NOT NULL,
                                   CONSTRAINT fk_table_dining_area
                                       FOREIGN KEY (dining_area_id) REFERENCES dining_areas (id)
                                           ON DELETE CASCADE
);

CREATE TABLE dishes (
                        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name          VARCHAR(255) NOT NULL,
                        description   TEXT,
                        price         DECIMAL(10,2) NOT NULL,
                        restaurant_id BIGINT NOT NULL,
                        CONSTRAINT fk_dish_restaurant
                            FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
                                ON DELETE CASCADE
);