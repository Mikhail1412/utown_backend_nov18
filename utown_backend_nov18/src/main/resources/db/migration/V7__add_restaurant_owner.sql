ALTER TABLE restaurants
    ADD COLUMN owner_id BIGINT NULL;

ALTER TABLE restaurants
    ADD CONSTRAINT fk_restaurants_owner
        FOREIGN KEY (owner_id) REFERENCES users(id);

UPDATE restaurants r
SET r.owner_id = (SELECT u.id FROM users u WHERE u.email = 'admin@utown.local')
WHERE r.owner_id IS NULL;

ALTER TABLE restaurants
    MODIFY owner_id BIGINT NOT NULL;
