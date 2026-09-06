package com.wpcc.seckillservice.seckill;

import org.springframework.stereotype.Service;

@Service
public class SeckillStockService {
  private final SeckillActivityMapper seckillActivityMapper;

  public SeckillStockService(
      SeckillActivityMapper seckillActivityMapper) {
    this.seckillActivityMapper = seckillActivityMapper;
  }

  public boolean tryDecreaseStock(
      Long activityId) {
    return seckillActivityMapper.decreaseStockIfAvailable(activityId) == 1;
  }
}
