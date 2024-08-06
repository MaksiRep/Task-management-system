CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR NOT NULL UNIQUE,
    password VARCHAR NOT NULL
);

CREATE TABLE task
(
    id          SERIAL PRIMARY KEY,
    header      VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    status      VARCHAR NOT NULL CHECK (status in ('WAITING', 'IN_PROGRESS', 'DONE')),
    priority    VARCHAR NOT NULL CHECK (priority in ('LOW', 'MIDDLE', 'HIGH')),
    author_id   INTEGER NOT NULL,
    executor_id INTEGER NOT NULL
);

create table comment
(
    id                SERIAL PRIMARY KEY,
    task_id           INTEGER NOT NULL,
    comment_author_id INTEGER NOT NULL,
    text              VARCHAR NOT NULL

);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);
ALTER TABLE task
    ADD CONSTRAINT FK_TASK_EXECUTOR FOREIGN KEY (executor_id) REFERENCES users (id);
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_TASK FOREIGN KEY (task_id) REFERENCES task (id);
ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_AUTHOR FOREIGN KEY (comment_author_id) REFERENCES users (id);