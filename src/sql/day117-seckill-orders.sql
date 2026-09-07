USE seckill_service;

CREATE TABLE seckill_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    seckill_price DECIMAL(10, 2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_seckill_orders_activity
        FOREIGN KEY (activity_id) REFERENCES seckill_activities(id),

    CONSTRAINT fk_seckill_orders_product
        FOREIGN KEY (product_id) REFERENCES products(id),

    CONSTRAINT uk_seckill_orders_activity_user
        UNIQUE (activity_id, user_id),

    INDEX idx_seckill_orders_user_created_at (user_id, created_at)
);