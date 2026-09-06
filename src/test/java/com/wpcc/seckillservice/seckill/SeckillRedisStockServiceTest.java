package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class SeckillRedisStockServiceTest {

  @Test
  void initializeStock_shouldSetActivityStockWithTtl() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    SeckillRedisStockService service = new SeckillRedisStockService(stringRedisTemplate);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

    service.initializeStock(1L, 10, Duration.ofMinutes(30));

    verify(valueOperations).set("seckill:stock:1", "10", Duration.ofMinutes(30));
  }

  @Test
  void tryDecreaseStock_shouldReturnTrueWhenRemainingStockIsNotNegative() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    SeckillRedisStockService service = new SeckillRedisStockService(stringRedisTemplate);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.decrement("seckill:stock:1")).thenReturn(9L);

    assertTrue(service.tryDecreaseStock(1L));
  }

  @Test
  void tryDecreaseStock_shouldCompensateAndReturnFalseWhenStockBecomesNegative() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    SeckillRedisStockService service = new SeckillRedisStockService(stringRedisTemplate);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.decrement("seckill:stock:1")).thenReturn(-1L);

    assertFalse(service.tryDecreaseStock(1L));
    verify(valueOperations).increment("seckill:stock:1");
  }
}
