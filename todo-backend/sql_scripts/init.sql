CREATE DATABASE IF NOT EXISTS todo_db;

CREATE USER 'todo_user'@'%' IDENTIFIED BY 'todo_password';

GRANT ALL PRIVILEGES ON todo_db.* TO 'todo_user'@'%';

FLUSH PRIVILEGES;