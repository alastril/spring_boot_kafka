INSERT INTO user (date_creation, user_name) VALUES
('2023-07-23 00:00:00.000000','root'),
('2023-04-24 00:00:00.000000','admin'),
('2023-07-24 00:00:00.000000','vasya'),
('2023-05-28 00:00:00.000000','kolya'),
('2023-07-25 00:00:00.000000','test'),
('2023-07-24 00:00:00.000000','account');
TRUNCATE user_security;
INSERT INTO user_security ( user_name, password, email, role) VALUES
('admin','$2a$10$pxDnMsgEOeLMMHjepOiGpuIa3dA4fEjOwJuH5m90UBlUJOGIYlfc.','admin@email.com','ROLE_ADMIN'),
('Tolya','$2a$10$pxDnMsgEOeLMMHjepOiGpuIa3dA4fEjOwJuH5m90UBlUJOGIYlfc.','email@com','ROLE_USER');