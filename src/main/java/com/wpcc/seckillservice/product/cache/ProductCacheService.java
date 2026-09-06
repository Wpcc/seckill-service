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

  private static final String NULL_VALUE = "__NULL__";
  private static final Duration NULL_EXPIRE_DURATION = Duration.ofMinutes(1);

  public ProductCacheService(
      StringRedisTemplate stringRedisTemplate,
      JsonMapper jsonMapper) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.jsonMapper = jsonMapper;
  }

  public ProductCacheResult getById(
      Long productId) {
    String key = buildKey(productId);
    String json = stringRedisTemplate.opsForValue().get(key);

    if (json == null) {
      return new ProductCacheResult(Optional.empty(), false);
    }

    if (NULL_VALUE.equals(json)) {
      return new ProductCacheResult(Optional.empty(), true);
    }

    try {
      ProductResponse product = jsonMapper.readValue(json, ProductResponse.class);
      return new ProductCacheResult(Optional.of(product), true);
    } catch (JacksonException exception) {
      stringRedisTemplate.delete(key);
      return new ProductCacheResult(Optional.empty(), false);
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

  public void putNotFound(
      Long productId) {
    try {
      stringRedisTemplate.opsForValue().set(buildKey(productId), NULL_VALUE, NULL_EXPIRE_DURATION);
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