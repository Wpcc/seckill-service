package com.wpcc.seckillservice.seckill.dto;

import java.math.BigDecimal;

public record SeckillCheckResponse(
    Long activityId,
    Long productId,
    Long userId,
    BigDecimal seckillPrice) {
}
