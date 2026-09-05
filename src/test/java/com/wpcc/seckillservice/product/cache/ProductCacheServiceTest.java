package com.wpcc.seckillservice.product.cache;

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

    assertTrue(productCacheService.getById(1L).isEmpty());
    verify(stringRedisTemplate).delete("seckill:product:detail:1");
  }
}
