package com.wpcc.seckillservice.seckill;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.wpcc.seckillservice.auth.CurrentUserContext;
import com.wpcc.seckillservice.product.ProductMapper;
import com.wpcc.seckillservice.seckill.dto.SeckillCheckResponse;

@Service
public class SeckillEligibilityService {
  private final SeckillActivityMapper seckillActivityMapper;
  private final ProductMapper productMapper;
  private final StringRedisTemplate stringRedisTemplate;
  private final SeckillRateLimitService seckillRateLimitService;

  private static final String REQUEST_KEY_PREFIX = "seckill:request:";

  public SeckillEligibilityService(
      SeckillActivityMapper seckillActivityMapper,
      ProductMapper productMapper,
      StringRedisTemplate stringRedisTemplate,
      SeckillRateLimitService seckillRateLimitService) {
    this.seckillActivityMapper = seckillActivityMapper;
    this.productMapper = productMapper;
    this.stringRedisTemplate = stringRedisTemplate;
    this.seckillRateLimitService = seckillRateLimitService;
  }

  public SeckillCheckResponse check(
      Long activityId) {
    Long userId = CurrentUserContext.requireUserId();

    seckillRateLimitService.checkAllowed(activityId, userId);

    SeckillActivity activity = seckillActivityMapper.findById((activityId))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "秒杀活动不存在"));

    if (!Byte.valueOf((byte) 1).equals(activity.getStatus())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "秒杀活动当前不可用");
    }

    LocalDateTime now = LocalDateTime.now();

    if (now.isBefore(activity.getStartTime())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "秒杀活动尚未开始");
    }

    if (!now.isBefore(activity.getEndTime())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "秒杀活动已结束");
    }

    if (activity.getSeckillStock() == null || activity.getSeckillStock() <= 0) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "秒杀库存不足");
    }

    if (productMapper.findById(activity.getProductId()).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "商品不存在或已下架");
    }

    String requestKey = buildRequestKey(activityId, userId);

    Boolean firstRequest = stringRedisTemplate.opsForValue().setIfAbsent(requestKey, "1", Duration.ofSeconds(3));

    if (!Boolean.TRUE.equals(firstRequest)) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试");
    }

    return new SeckillCheckResponse(activity.getId(), activity.getProductId(), userId, activity.getSeckillPrice());
  }

  private String buildRequestKey(
      Long activityId,
      Long userId) {
    return REQUEST_KEY_PREFIX + activityId + ":" + userId;
  }
}
