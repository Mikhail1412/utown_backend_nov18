INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('RESTAURANT_ADMIN');
INSERT INTO roles (name) VALUES ('ADMIN');

INSERT INTO users (first_name, last_name, name, age, email, password)
VALUES
    ('Test2', 'User', 'Test2 User', 25, 'test2_user@example.com', '$2b$12$YIXthxxp549pLXORcsaLKuZmN1gLeCsvlSPDF6ZCBFbo90ZsMeiN6'),
    ('Test3', 'User', 'Test3 User', 25, 'test3_user@example.com', '$2b$12$YIXthxxp549pLXORcsaLKuZmN1gLeCsvlSPDF6ZCBFbo90ZsMeiN6'),
    ('Admin', 'User', 'Admin User', 30, 'admin@utown.local', '$2b$12$YIXthxxp549pLXORcsaLKuZmN1gLeCsvlSPDF6ZCBFbo90ZsMeiN6');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'test2_user@example.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'test3_user@example.com' AND r.name = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@utown.local' AND r.name = 'ADMIN';

INSERT INTO restaurants (name, address, phone_number, description, status)
VALUES
    ('Utown BBQ', 'Seoul, Gangnam-gu, Teheran-ro 1', '010-1111-1111', 'BBQ restaurant', 'OPEN'),
    ('Utown Sushi', 'Incheon, Yeonsu-gu, Songdo 1', '010-2222-2222', 'Sushi restaurant', 'OPEN');

INSERT INTO dining_areas (name, description, restaurant_id)
VALUES
    ('Main Hall', 'Main dining hall', 1),
    ('VIP', 'VIP zone', 1),
    ('Hall', 'Common hall', 2);

INSERT INTO restaurant_tables (table_number, capacity, active, dining_area_id)
VALUES
    ('A1', 4, TRUE, 1),
    ('A2', 2, TRUE, 1),
    ('V1', 6, TRUE, 2),
    ('S1', 4, TRUE, 3);

INSERT INTO dishes (name, description, price, restaurant_id, active)
VALUES
    ('Pork BBQ', 'Grilled pork', 13000.00, 1, TRUE),
    ('Beef BBQ', 'Grilled beef', 20000.00, 1, TRUE),
    ('Kimchi', 'Spicy kimchi', 3000.00, 1, TRUE),
    ('Salmon Sushi', 'Salmon nigiri', 15000.00, 2, TRUE),
    ('Tuna Sushi', 'Tuna nigiri', 16000.00, 2, TRUE);
