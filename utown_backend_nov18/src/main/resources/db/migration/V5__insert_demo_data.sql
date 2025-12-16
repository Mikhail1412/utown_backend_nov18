INSERT INTO restaurants (name, status, owner_id)
VALUES ('Demo Restaurant', 'OPEN', NULL);

INSERT INTO restaurant_tables (restaurant_id, table_number, seats)
SELECT r.id, 1, 2
FROM restaurants r
WHERE r.name = 'Demo Restaurant';

INSERT INTO restaurant_tables (restaurant_id, table_number, seats)
SELECT r.id, 2, 4
FROM restaurants r
WHERE r.name = 'Demo Restaurant';

INSERT INTO dishes (restaurant_id, name, price, active)
SELECT r.id, 'Kimchi Fried Rice', 12000.00, 1
FROM restaurants r
WHERE r.name = 'Demo Restaurant';

INSERT INTO dishes (restaurant_id, name, price, active)
SELECT r.id, 'Bibimbap', 13000.00, 1
FROM restaurants r
WHERE r.name = 'Demo Restaurant';

INSERT INTO dishes (restaurant_id, name, price, active)
SELECT r.id, 'Tteokbokki', 9000.00, 1
FROM restaurants r
WHERE r.name = 'Demo Restaurant';

INSERT INTO dishes (restaurant_id, name, price, active)
SELECT r.id, 'Bulgogi', 15000.00, 1
FROM restaurants r
WHERE r.name = 'Demo Restaurant';

INSERT INTO dishes (restaurant_id, name, price, active)
SELECT r.id, 'Cold Noodles', 11000.00, 0
FROM restaurants r
WHERE r.name = 'Demo Restaurant';
