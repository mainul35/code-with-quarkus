CREATE DATABASE IF NOT EXISTS fruits_db;

-- Change this line:
CREATE USER 'fruits_db_user'@'%' IDENTIFIED BY 'fruits_db_password';

GRANT ALL PRIVILEGES ON fruits_db.* TO 'fruits_db_user'@'%';
FLUSH PRIVILEGES;
