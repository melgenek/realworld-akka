CREATE TABLE IF NOT EXISTS users (
  id BIGINT auto_increment PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  bio TEXT,
  image VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS relations (
  follower_id BIGINT NOT NULL,
  followee_id BIGINT NOT NULL,
  PRIMARY KEY (follower_id, followee_id),
  CONSTRAINT FOLLOWER_FK FOREIGN KEY (follower_id) references users(id),
  CONSTRAINT FOLLOWEE_FK FOREIGN KEY (followee_id) references users(id)
);
