USE seckill_service;

CREATE TABLE seckill_activities (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  seckill_price DECIMAL(10, 2) NOT NULL,
  seckill_stock INT NOT NULL,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  CONSTRAINT fk_seckill_activities_product
    FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT ck_seckill_activities_price
    CHECK (seckill_price >= 0),
  CONSTRAINT ck_seckill_activities_stock
    CHECK (seckill_stock >= 0),
  CONSTRAINT ck_seckill_activities_time
    CHECK (end_time > start_time),

  INDEX idx_seckill_activities_product_status (product_id, status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;