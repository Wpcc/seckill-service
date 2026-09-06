package com.wpcc.seckillservice.product.cache;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import tools.jackson.databind.json.JsonMapper;

class ProductCacheServiceTest {

  @Test
  void getById_shouldDeleteInvalidJsonAndReturnEmpty() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    ProductCacheService productCacheService = new ProductCacheService(
        stringRedisTemplate,
        JsonMapper.builder().build());

    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get("seckill:product:detail:1")).thenReturn("invalid-json");

    ProductCacheResult result = productCacheService.getById(1L);

    assertTrue(result.product().isEmpty());
    assertFalse(result.fromCache());
    verify(stringRedisTemplate).delete("seckill:product:detail:1");
  }

  @Test
  void getById_shouldReturnCachedEmptyResultWhenNullValueIsFound() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    ProductCacheService productCacheService = new ProductCacheService(
        stringRedisTemplate,
        JsonMapper.builder().build());

    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.get("seckill:product:detail:999")).thenReturn("__NULL__");

    ProductCacheResult result = productCacheService.getById(999L);

    assertTrue(result.product().isEmpty());
    assertTrue(result.fromCache());
  }

  @Test
  void putNotFound_shouldCacheNullValueForOneMinute() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    ProductCacheService productCacheService = new ProductCacheService(
        stringRedisTemplate,
        JsonMapper.builder().build());

    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

    productCacheService.putNotFound(999L);

    verify(valueOperations).set(
        "seckill:product:detail:999",
        "__NULL__",
        java.time.Duration.ofMinutes(1));
  }
}
