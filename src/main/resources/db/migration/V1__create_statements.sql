CREATE TABLE IF NOT EXISTS users(
    user_id INTEGER NOT NULL,
    name varchar(255),
    creation_date timestamp with time zone default now(),
    PRIMARY KEY(user_id)

);

CREATE INDEX IF NOT EXISTS user_id_index on users USING btree (user_id);


CREATE TABLE IF NOT EXISTS user_friends_mapping(
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    connection_date timestamp with time zone default now(),
    FOREIGN KEY (user_id) REFERENCES users (user_id) MATCH SIMPLE ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) MATCH SIMPLE ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS user_id_friend_id_index on user_friends_mapping USING btree (user_id, friend_id);


