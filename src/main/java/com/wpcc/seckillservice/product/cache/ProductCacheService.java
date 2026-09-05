package com.wpcc.seckillservice.product.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.product.dto.ProductResponse;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
public class ProductCacheService {

  private static final String KEY_PREFIX = "seckill:product:detail:";
  private static final Duration EXPIRE_DURATION = Duration.ofMinutes(10);

  private final StringRedisTemplate stringRedisTemplate;
  private final JsonMapper jsonMapper;

  public ProductCacheService(
      StringRedisTemplate stringRedisTemplate,
      JsonMapper jsonMapper) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jsonMapper = jsonMapper;
  }

  public Optional<ProductResponse> getById(
      Long productId) {
    String key = buildKey(productId);
    String json = stringRedisTemplate.opsForValue().get(key);

    if (json == null) {
      return Optional.empty();
    }

    try {
      ProductResponse product = jsonMapper.readValue(json, ProductResponse.class);
      return Optional.of(product);
    } catch (JacksonException exception) {
      stringRedisTemplate.delete(key);
      return Optional.empty();
    }
  }

  public void put(
      ProductResponse product) {
    try {
      String json = jsonMapper.writeValueAsString(product);

      stringRedisTemplate.opsForValue().set(buildKey(product.id()), json, EXPIRE_DURATION);
    } catch (JacksonException exception) {
      throw new IllegalStateException("商品缓存序列化失败", exception);
    }
  }

  public void evict(
      Long productId) {
    stringRedisTemplate.delete(buildKey(productId));
  }

  private String buildKey(
      Long productId) {
    return KEY_PREFIX + productId;
  }
}