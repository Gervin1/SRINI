-- Create users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- Insert three users
INSERT INTO users (username, password) VALUES ('user1', '$2a$10$rXH2MjM9rR8uLQwsJIx1gO2wpRIi3xgSIYFOk8IZxJDAClT4/jEEK');
INSERT INTO users (username, password) VALUES ('user2', '$2a$10$wIKkKL5reXj3tsYWy3/Nme78suJhKzlcmhWfXHNO2xRXFA/vnvwfm');
INSERT INTO users (username, password) VALUES ('user3', '$2a$10$GfLqiWlFsJ/1mRCc2aPLne2CgeV1gTkJiD6qchgbu1jzKNFdojYiK');

-- Create countries table
CREATE TABLE countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Insert countries
INSERT INTO countries (name) VALUES ('Estonia');
INSERT INTO countries (name) VALUES ('Latvia');
INSERT INTO countries (name) VALUES ('Lithuania');
INSERT INTO countries (name) VALUES ('Finland');
INSERT INTO countries (name) VALUES ('Sweden');

-- Create clients table
CREATE TABLE clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(100) NOT NULL,
    country_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (country_id) REFERENCES countries(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
