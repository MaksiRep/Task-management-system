INSERT INTO users (id, email, password)
VALUES (1, 'email1@mail.ru', '1b4f0e9851971998e732078544c96b36c3d01cedf7caa332359d6f1d83567014'),
       (2, 'email2@mail.ru', '60303ae22b998861bce3b28f33eec1be758a213c86c93c076dbe9f558c11c752');

INSERT INTO task (id, header, description, status, priority, author_id, executor_id)
VALUES (1, 'header1', 'description', 'IN_PROGRESS', 'LOW', 1, 1),
       (2, 'header2', 'description', 'WAITING', 'HIGH', 2, 2),
       (3, 'header3', 'NO', 'WAITING', 'MIDDLE', 2, 1),
       (4, 'header4', 'NO', 'DONE', 'LOW', 1, 2);

INSERT INTO comment (id, task_id, comment_author_id, text)
VALUES (1, 1, 1, 'text1'),
       (2, 1, 2, 'text2'),
       (3, 2, 1, 'text3'),
       (4, 2, 2, 'text4');