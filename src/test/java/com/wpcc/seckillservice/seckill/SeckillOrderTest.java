package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Options;
import org.junit.jupiter.api.Test;

class SeckillOrderTest {

  @Test
  void setters_shouldAllowMyBatisToPopulateAnOrderAndGeneratedId() {
    LocalDateTime createdAt = LocalDateTime.of(2026, 9, 7, 10, 30);
    SeckillOrder order = new SeckillOrder();

    order.setId(10L);
    order.setActivityId(1L);
    order.setUserId(100L);
    order.setProductId(20L);
    order.setSeckillPrice(new BigDecimal("9.90"));
    order.setStatus(1);
    order.setCreatedAt(createdAt);

    assertEquals(10L, order.getId());
    assertEquals(1L, order.getActivityId());
    assertEquals(100L, order.getUserId());
    assertEquals(20L, order.getProductId());
    assertEquals(new BigDecimal("9.90"), order.getSeckillPrice());
    assertEquals(1, order.getStatus());
    assertEquals(createdAt, order.getCreatedAt());
  }

  @Test
  void insert_shouldEnableGeneratedKeyWriteBackToId() throws NoSuchMethodException {
    Method insertMethod = SeckillOrderMapper.class.getMethod("insert", SeckillOrder.class);
    Options options = insertMethod.getAnnotation(Options.class);

    assertTrue(options.useGeneratedKeys());
    assertEquals("id", options.keyProperty());
  }
}
