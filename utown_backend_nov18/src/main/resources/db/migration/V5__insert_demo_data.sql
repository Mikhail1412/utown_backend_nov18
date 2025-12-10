INSERT INTO restaurants (id, name, description, address, phone) VALUES
                                                                    (1, 'Utown Korean BBQ', 'Корейский гриль и супы', 'Incheon, Block A-1', '010-1111-2222'),
                                                                    (2, 'Utown Pizza & Pasta', 'Пицца, паста и салаты', 'Incheon, Block B-2', '010-2222-3333'),
                                                                    (3, 'Utown Coffee & Desserts', 'Кофе, выпечка и десерты', 'Incheon, Block C-3', '010-3333-4444');

INSERT INTO dining_areas (id, restaurant_id, name, description) VALUES
                                                                    (1, 1, 'Основной зал', 'Главный зал с мангалами'),
                                                                    (2, 1, 'VIP-комнаты', 'Приватные комнаты'),
                                                                    (3, 2, 'Основной зал', 'Основной зал пиццерии'),
                                                                    (4, 2, 'Терраса', 'Уличные столики'),
                                                                    (5, 3, 'Бариста зона', 'Барная стойка'),
                                                                    (6, 3, 'Кофейный зал', 'Основной зал кофейни');

INSERT INTO restaurant_tables (id, dining_area_id, table_number, capacity) VALUES
                                                                               (1, 1, 'A1', 4),
                                                                               (2, 1, 'A2', 4),
                                                                               (3, 1, 'A3', 6),
                                                                               (4, 2, 'VIP1', 6),
                                                                               (5, 2, 'VIP2', 8),
                                                                               (6, 3, 'P1', 2),
                                                                               (7, 3, 'P2', 4),
                                                                               (8, 4, 'T1', 4),
                                                                               (9, 4, 'T2', 4),
                                                                               (10, 5, 'B1', 2),
                                                                               (11, 6, 'C1', 2),
                                                                               (12, 6, 'C2', 4);

INSERT INTO dishes (id, restaurant_id, name, description, price, is_active) VALUES
                                                                                (1, 1, 'Samgyeopsal', 'Свиная грудинка на гриле', 15000, 1),
                                                                                (2, 1, 'Kimchi Jjigae', 'Острый суп с кимчи', 9000, 1),
                                                                                (3, 1, 'Bibimbap', 'Рис с овощами и говядиной', 10000, 1),
                                                                                (4, 2, 'Margherita Pizza', 'Пицца Маргарита', 13000, 1),
                                                                                (5, 2, 'Pepperoni Pizza', 'Пицца Пепперони', 15000, 1),
                                                                                (6, 2, 'Carbonara Pasta', 'Паста карбонара', 12000, 1),
                                                                                (7, 3, 'Americano', 'Классический американо', 4000, 1),
                                                                                (8, 3, 'Caffe Latte', 'Латте', 4500, 1),
                                                                                (9, 3, 'Tiramisu', 'Тирамису', 7000, 1);
