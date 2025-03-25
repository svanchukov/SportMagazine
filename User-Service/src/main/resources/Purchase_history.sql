CREATE TABLE user_purchase_history (
    user_id BIGINT NOT NULL,
    purchase VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);