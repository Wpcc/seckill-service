package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.wpcc.seckillservice.auth.CurrentUserContext;
import com.wpcc.seckillservice.product.Product;
import com.wpcc.seckillservice.product.ProductMapper;
import com.wpcc.seckillservice.seckill.dto.SeckillCheckResponse;

class SeckillEligibilityServiceTest {

  @Test
  void check_shouldReturnActivityInformationForEligibleUser() {
    TestContext context = testContext();
    when(context.activityMapper.findById(1L)).thenReturn(Optional.of(activeActivity()));
    when(context.productMapper.findById(1L)).thenReturn(Optional.of(new Product()));
    when(context.valueOperations.setIfAbsent(
        "seckill:request:1:7",
        "1",
        java.time.Duration.ofSeconds(3))).thenReturn(true);

    CurrentUserContext.setUserId(7L);
    try {
      SeckillCheckResponse response = context.service.check(1L);

      assertEquals(1L, response.activityId());
      assertEquals(1L, response.productId());
      assertEquals(7L, response.userId());
      assertEquals(new BigDecimal("5999.00"), response.seckillPrice());
    } finally {
      CurrentUserContext.clear();
    }
  }

  @Test
  void check_shouldRejectActivityThatHasNotStarted() {
    TestContext context = testContext();
    SeckillActivity activity = activity(
        LocalDateTime.now().plusMinutes(1),
        LocalDateTime.now().plusMinutes(30));
    when(context.activityMapper.findById(1L)).thenReturn(Optional.of(activity));

    CurrentUserContext.setUserId(7L);
    try {
      ResponseStatusException exception = assertThrows(
          ResponseStatusException.class,
          () -> context.service.check(1L));

      assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
      assertEquals("秒杀活动尚未开始", exception.getReason());
    } finally {
      CurrentUserContext.clear();
    }
  }

  @Test
  void check_shouldRejectProductThatDoesNotExistOrIsOffSale() {
    TestContext context = testContext();
    when(context.activityMapper.findById(1L)).thenReturn(Optional.of(activeActivity()));
    when(context.productMapper.findById(1L)).thenReturn(Optional.empty());

    CurrentUserContext.setUserId(7L);
    try {
      ResponseStatusException exception = assertThrows(
          ResponseStatusException.class,
          () -> context.service.check(1L));

      assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
      assertEquals("商品不存在或已下架", exception.getReason());
    } finally {
      CurrentUserContext.clear();
    }
  }

  @Test
  void check_shouldRejectRepeatedRequestWithinThreeSeconds() {
    TestContext context = testContext();
    when(context.activityMapper.findById(1L)).thenReturn(Optional.of(activeActivity()));
    when(context.productMapper.findById(1L)).thenReturn(Optional.of(new Product()));
    when(context.valueOperations.setIfAbsent(
        "seckill:request:1:7",
        "1",
        java.time.Duration.ofSeconds(3))).thenReturn(false);

    CurrentUserContext.setUserId(7L);
    try {
      ResponseStatusException exception = assertThrows(
          ResponseStatusException.class,
          () -> context.service.check(1L));

      assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
      assertEquals("请求过于频繁，请稍后再试", exception.getReason());
    } finally {
      CurrentUserContext.clear();
    }
  }

  private TestContext testContext() {
    SeckillActivityMapper activityMapper = mock(SeckillActivityMapper.class);
    ProductMapper productMapper = mock(ProductMapper.class);
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    SeckillRateLimitService seckillRateLimitService = mock(SeckillRateLimitService.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);

    SeckillEligibilityService service = new SeckillEligibilityService(
        activityMapper,
        productMapper,
        stringRedisTemplate,
        seckillRateLimitService);
    return new TestContext(service, activityMapper, productMapper, valueOperations);
  }

  private SeckillActivity activeActivity() {
    return activity(LocalDateTime.now().minusMinutes(1), LocalDateTime.now().plusMinutes(1));
  }

  private SeckillActivity activity(LocalDateTime startTime, LocalDateTime endTime) {
    SeckillActivity activity = new SeckillActivity(
        1L,
        new BigDecimal("5999.00"),
        10,
        startTime,
        endTime,
        (byte) 1);
    activity.setId(1L);
    return activity;
  }

  private record TestContext(
      SeckillEligibilityService service,
      SeckillActivityMapper activityMapper,
      ProductMapper productMapper,
      ValueOperations<String, String> valueOperations) {
  }
}
