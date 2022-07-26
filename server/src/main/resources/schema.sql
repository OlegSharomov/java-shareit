DROP TABLE IF EXISTS comments,bookings, items, requests, users;
CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(2048),
    requestor_id BIGINT,
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_requestor_id FOREIGN KEY (requestor_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(512),
    is_available BOOLEAN      NOT NULL,
    owner_id     BIGINT,
    request_id   BIGINT,
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_request_id FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(20) NOT NULL,
    CONSTRAINT fk_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_booker_id FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text      VARCHAR(2048),
    item_id   BIGINT,
    created   TIMESTAMP WITHOUT TIME ZONE,
    author_id BIGINT,
    CONSTRAINT fk_comment_item_id FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_booker_id FOREIGN KEY (author_id) REFERENCES users (id)
);