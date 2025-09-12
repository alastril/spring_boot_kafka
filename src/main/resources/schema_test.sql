DELIMITER $$
CREATE PROCEDURE `user`()
BEGIN
  IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'user' AND TABLE_SCHEMA = 'kafka_db') AND
	NOT EXISTS (SELECT 1 FROM user)
THEN
	INSERT INTO user (date_creation, user_name) VALUES
        ('2023-07-23 00:00:00.000000','root'),
        ('2023-07-24 00:00:00.000000','root'),
        ('2023-04-24 00:00:00.000000','admin'),
        ('2023-05-24 00:00:00.000000','vasya'),
        ('2023-05-28 00:00:00.000000','kolya'),
        ('2023-07-08 00:00:00.000000','test'),
        ('2023-07-07 00:00:00.000000','account');
  END IF;
END;
CREATE PROCEDURE `user_security`()
BEGIN
  IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'user_security' AND TABLE_SCHEMA = 'kafka_db') AND
	NOT EXISTS (SELECT 1 FROM user_security)
THEN
	TRUNCATE user_security;
            INSERT INTO user_security ( user_name, password, email, role) VALUES
            ('admin','$2a$10$pxDnMsgEOeLMMHjepOiGpuIa3dA4fEjOwJuH5m90UBlUJOGIYlfc.','admin@email.com','ROLE_ADMIN'),
            ('Tolya','$2a$10$pxDnMsgEOeLMMHjepOiGpuIa3dA4fEjOwJuH5m90UBlUJOGIYlfc.','email@com','ROLE_USER');
  END IF;
END;
CREATE PROCEDURE `orders`()
BEGIN
  IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'orders' AND TABLE_SCHEMA = 'kafka_db') AND
	NOT EXISTS (SELECT 1 FROM orders)
THEN
	INSERT INTO orders ( number_order, customer_id) VALUES
            ('2123_143344_1',1),
            ('test_123_1',1),
            ('order_7_1',1),
            ('order_6_1',1),
            ('order_5_1',1),
            ('order_4_1',1),
            ('order_3_1',1),
            ('order_1_2',2);
  END IF;
END $$
CALL user();
DROP PROCEDURE `user`;


CALL orders();
DROP PROCEDURE `orders`;

CALL user_security();
DROP PROCEDURE `user_security`;