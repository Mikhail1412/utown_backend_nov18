CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        restaurant_id BIGINT NOT NULL,
                        table_id BIGINT NULL,
                        status VARCHAR(50) NOT NULL,
                        total_price INT NOT NULL DEFAULT 0,

                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
                        CONSTRAINT fk_orders_table FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

CREATE TABLE order_items (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             dish_id BIGINT NOT NULL,
                             quantity INT NOT NULL,
                             price_at_moment INT NOT NULL,

                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
                             CONSTRAINT fk_order_items_dish FOREIGN KEY (dish_id) REFERENCES dishes(id)
);
