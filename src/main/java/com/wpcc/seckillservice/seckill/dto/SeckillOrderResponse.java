package com.wpcc.seckillservice.seckill.dto;

import java.math.BigDecimal;

public record SeckillOrderResponse(
    Long orderId,
    Long activityId,
    Long productId,
    BigDecimal seckillPrice,
    String status) {

}
