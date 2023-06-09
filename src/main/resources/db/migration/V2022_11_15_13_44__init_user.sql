create table user_role
(
    user_id INT          NOT NULL,
    role    VARCHAR(255) NOT NULL
);

create table users
(
    id        SERIAL PRIMARY KEY NOT NULL,
    password  VARCHAR(255)       NOT NULL,
    username  VARCHAR(255)       NOT NULL,
    first_name VARCHAR(255)       NOT NULL,
    last_name  VARCHAR(255)       NOT NULL,
    UNIQUE (username)
);

alter table if exists user_role
    add constraint user_role_user_fk
    foreign key (user_id) references users (id);

