package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

class RedisDistributedLockServiceTest {

  @Test
  void tryLock_shouldReturnTrueWhenRedisAcquiresLock() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    RedisDistributedLockService service = new RedisDistributedLockService(stringRedisTemplate, mockScript());
    Duration leaseTime = Duration.ofSeconds(10);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent("seckill:lock:activity:1", "request-a", leaseTime)).thenReturn(true);

    assertTrue(service.tryLock("activity:1", "request-a", leaseTime));
  }

  @Test
  void tryLock_shouldReturnFalseWhenAnotherOwnerAlreadyHoldsLock() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    RedisDistributedLockService service = new RedisDistributedLockService(stringRedisTemplate, mockScript());
    Duration leaseTime = Duration.ofSeconds(10);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.setIfAbsent("seckill:lock:activity:1", "request-b", leaseTime)).thenReturn(false);

    assertFalse(service.tryLock("activity:1", "request-b", leaseTime));
  }

  @Test
  void unlock_shouldReturnTrueWhenOwnerMatchesAndScriptDeletesLock() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    DefaultRedisScript<Long> unlockScript = mockScript();
    RedisDistributedLockService service = new RedisDistributedLockService(stringRedisTemplate, unlockScript);
    when(stringRedisTemplate.execute(eq(unlockScript), eq(List.of("seckill:lock:activity:1")), eq("request-a")))
        .thenReturn(1L);

    assertTrue(service.unlock("activity:1", "request-a"));
    verify(stringRedisTemplate).execute(eq(unlockScript), eq(List.of("seckill:lock:activity:1")), eq("request-a"));
  }

  @Test
  void unlock_shouldReturnFalseWhenOwnerDoesNotMatch() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    DefaultRedisScript<Long> unlockScript = mockScript();
    RedisDistributedLockService service = new RedisDistributedLockService(stringRedisTemplate, unlockScript);
    when(stringRedisTemplate.execute(eq(unlockScript), eq(List.of("seckill:lock:activity:1")), eq("request-b")))
        .thenReturn(0L);

    assertFalse(service.unlock("activity:1", "request-b"));
  }

  @Test
  void unlock_shouldThrowWhenRedisScriptReturnsNull() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    RedisDistributedLockService service = new RedisDistributedLockService(stringRedisTemplate, mockScript());

    assertThrows(IllegalStateException.class, () -> service.unlock("activity:1", "request-a"));
  }

  @SuppressWarnings("unchecked")
  private DefaultRedisScript<Long> mockScript() {
    return mock(DefaultRedisScript.class);
  }
}
