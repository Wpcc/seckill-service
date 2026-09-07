package com.wpcc.seckillservice.seckill;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisDistributedLockService {
  private static final String LOCK_KEY_PREFIX = "seckill:lock:";

  private final StringRedisTemplate stringRedisTemplate;
  private final DefaultRedisScript<Long> redisUnlockScript;

  public RedisDistributedLockService(
      StringRedisTemplate stringRedisTemplate,
      @Qualifier("redisUnlockScript")
      DefaultRedisScript<Long> redisUnlockScript) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.redisUnlockScript = redisUnlockScript;
  }

  public boolean tryLock(
      String lockName,
      String lockOwner,
      Duration leaseTime) {
    Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(buildLockKey(lockName), lockOwner, leaseTime);

    return Boolean.TRUE.equals(locked);
  }

  public boolean unlock(
      String lockName,
      String lockOwner) {
    Long result = stringRedisTemplate.execute(redisUnlockScript, List.of(buildLockKey(lockName)), lockOwner);

    if (result == null) {
      throw new IllegalStateException("Redis 解锁脚本执行失败");
    }

    return result == 1L;
  }

  private String buildLockKey(
      String lockName) {
    return LOCK_KEY_PREFIX + lockName;
  }

}
