INSERT INTO users (user_id, login, name, email, password)
VALUES (1, 'admin', 'admin', 'admin@email.com', '$2a$10$kDq7eY8dHa.uEsX/LcL.p.W3YWZTMBsXqfpfMyVLH/HCJ0/uTy.FG');

INSERT INTO users_roles VALUES (1, 1);
INSERT INTO users_roles VALUES (2, 1);