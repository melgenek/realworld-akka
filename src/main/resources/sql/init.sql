CREATE TABLE IF NOT EXISTS users (
  id BIGINT auto_increment PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  username VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  bio TEXT,
  image VARCHAR(500)
);
