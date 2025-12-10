INSERT INTO users (first_name, last_name, name, age, email, password)
VALUES (
           'Dev',
           'Admin',
           'Dev Admin',
           30,
           'admin@utown.local',
           '$2b$12$YIXthxxp549pLXORcsaLKuZmN1gLeCsvlSPDF6ZCBFbo90ZsMeiN6'
       );

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@utown.local'
  AND r.name = 'ROLE_ADMIN';
