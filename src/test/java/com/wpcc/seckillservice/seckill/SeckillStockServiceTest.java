package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

class SeckillStockServiceTest {

  @Test
  void tryDecreaseStock_shouldReturnTrueWhenOneRowIsUpdated() {
    SeckillActivityMapper seckillActivityMapper = mock(SeckillActivityMapper.class);
    SeckillStockService seckillStockService = new SeckillStockService(seckillActivityMapper);
    when(seckillActivityMapper.decreaseStockIfAvailable(1L)).thenReturn(1);

    boolean decreased = seckillStockService.tryDecreaseStock(1L);

    assertTrue(decreased);
    verify(seckillActivityMapper).decreaseStockIfAvailable(1L);
  }

  @Test
  void tryDecreaseStock_shouldReturnFalseWhenNoRowIsUpdated() {
    SeckillActivityMapper seckillActivityMapper = mock(SeckillActivityMapper.class);
    SeckillStockService seckillStockService = new SeckillStockService(seckillActivityMapper);
    when(seckillActivityMapper.decreaseStockIfAvailable(1L)).thenReturn(0);

    boolean decreased = seckillStockService.tryDecreaseStock(1L);

    assertFalse(decreased);
  }
}
