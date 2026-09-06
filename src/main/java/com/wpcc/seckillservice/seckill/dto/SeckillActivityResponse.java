package com.wpcc.seckillservice.seckill.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SeckillActivityResponse(
    Long id,
    Long productId,
    BigDecimal seckillPrice,
    Integer seckillStock,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Byte status) {

}
