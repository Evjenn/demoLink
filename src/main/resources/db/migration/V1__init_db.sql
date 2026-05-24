CREATE TABLE users (
       id BIGSERIAL PRIMARY KEY,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       role VARCHAR(50) NOT NULL
);

CREATE TABLE links (
       id BIGSERIAL PRIMARY KEY,
       original_link VARCHAR(2048) NOT NULL,
       short_link VARCHAR(255) UNIQUE NOT NULL,
       link_follows BIGINT NOT NULL,
       created_at TIMESTAMP NOT NULL,
       expires_at TIMESTAMP NOT NULL,
       active BOOLEAN NOT NULL,
       user_id BIGINT NOT NULL REFERENCES users(id)
);