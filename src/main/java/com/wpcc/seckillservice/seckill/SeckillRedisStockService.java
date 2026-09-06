package com.wpcc.seckillservice.seckill;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillRedisStockService {
  private static final String KEY_PREFIX = "seckill:stock:";

  private final StringRedisTemplate stringRedisTemplate;

  public SeckillRedisStockService(
      StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public void initializeStock(
      Long activityId,
      int stock,
      Duration ttl) {
    stringRedisTemplate.opsForValue().set(buildKey(activityId), String.valueOf(stock), ttl);
  }

  public boolean tryDecreaseStock(
      Long activityId) {
    Long remaining = stringRedisTemplate.opsForValue().decrement(buildKey(activityId));

    if (remaining == null) {
      throw new IllegalStateException("Redis 秒杀库存扣减失败");
    }

    if (remaining >= 0) {
      return true;
    }

    increaseStock(activityId);
    return false;
  }

  public void increaseStock(
      Long activityId) {
    stringRedisTemplate.opsForValue().increment(buildKey(activityId));
  }

  private String buildKey(
      Long activityId) {
    return KEY_PREFIX + activityId;
  }
}
