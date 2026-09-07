package com.wpcc.seckillservice.seckill;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class SeckillLuaStockService {
  private static final String STOCK_KEY_PREFIX = "seckill:stock:";
  private static final String PURCHASED_USERS_KEY_PREFIX = "seckill:purchased-users:";

  private final StringRedisTemplate stringRedisTemplate;

  private final DefaultRedisScript<Long> seckillLuaScript;

  public SeckillLuaStockService(
      StringRedisTemplate stringRedisTemplate,
      @Qualifier("seckillLuaScript")
      DefaultRedisScript<Long> seckillLuaScript) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.seckillLuaScript = seckillLuaScript;
  }

  public SeckillLuaStockResult trySeckill(
      Long activityId,
      Long userId,
      Duration userTtl) {
    Long result = stringRedisTemplate.execute(seckillLuaScript,
        List.of(buildStockKey(activityId), buildPurchasedUsersKey(activityId)), String.valueOf(userId),
        String.valueOf(userTtl.toSeconds()));

    if (result == null) {
      throw new IllegalStateException("Redis 秒杀 Lua 脚本执行失败");
    }

    return switch (result.intValue()) {
    case 0 -> SeckillLuaStockResult.SUCCESS;
    case 1 -> SeckillLuaStockResult.OUT_OF_STOCK;
    case 2 -> SeckillLuaStockResult.DUPLICATE_PURCHASE;
    default -> throw new IllegalStateException("未知的秒杀脚本返回值：" + result);
    };
  }

  private String buildStockKey(
      Long activityId) {
    return STOCK_KEY_PREFIX + activityId;
  }

  private String buildPurchasedUsersKey(
      Long activityId) {
    return PURCHASED_USERS_KEY_PREFIX + activityId;
  }

  public enum SeckillLuaStockResult {
    SUCCESS, // Lua 返回 0：秒杀成功
    OUT_OF_STOCK, // Lua 返回 1：库存不足
    DUPLICATE_PURCHASE // Lua 返回 2：用户已购买
  }
}
