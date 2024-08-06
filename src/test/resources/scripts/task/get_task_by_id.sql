TRUNCATE task CASCADE;
TRUNCATE users CASCADE;

INSERT INTO users (id, email, password)
VALUES (1, 'email1@mail.ru', '1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014');

INSERT INTO task (id, header, description, status, priority, author_id, executor_id)
VALUES (1, 'header', 'description', 'WAITING', 'LOW', 1, 1);