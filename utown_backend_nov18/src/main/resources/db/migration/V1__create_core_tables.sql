CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       name VARCHAR(255),
                       age INT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE restaurants (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
                             owner_id BIGINT NULL,
                             CONSTRAINT fk_restaurants_owner
                                 FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE restaurant_tables (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   restaurant_id BIGINT NOT NULL,
                                   table_number INT NOT NULL,
                                   seats INT NOT NULL DEFAULT 2,
                                   CONSTRAINT fk_restaurant_tables_restaurant
                                       FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

CREATE TABLE dishes (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        restaurant_id BIGINT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        price DECIMAL(19,2) NOT NULL DEFAULT 0.00,
                        active TINYINT(1) NOT NULL DEFAULT 1,
                        CONSTRAINT fk_dishes_restaurant
                            FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);
