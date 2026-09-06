package com.wpcc.seckillservice.seckill;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SeckillRateLimitService {

  StringRedisTemplate stringRedisTemplate;

  private static final String KEY_PREFIX = "seckill:rate-limit:";
  private static final long LIMIT = 5;
  private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);

  public SeckillRateLimitService(
      StringRedisTemplate stringRedisTemplate) {
    this.stringRedisTemplate = stringRedisTemplate;
  }

  public void checkAllowed(
      Long activityId,
      Long userId) {
    long windowId = System.currentTimeMillis() / 60_000;
    String key = KEY_PREFIX + activityId + ":" + userId + ":" + windowId;

    Long count = stringRedisTemplate.opsForValue().increment(key);

    if (count == null) {
      throw new IllegalStateException("Redis 限流计数失败");
    } else if (count == 1) {
      stringRedisTemplate.expire(key, WINDOW_DURATION);
    } else if (count > LIMIT) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
    }
  }
}
