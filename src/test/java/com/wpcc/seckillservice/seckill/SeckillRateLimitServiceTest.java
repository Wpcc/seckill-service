package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class SeckillRateLimitServiceTest {

  @Test
  void checkAllowed_shouldSetOneMinuteExpiryForFirstRequest() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    SeckillRateLimitService service = new SeckillRateLimitService(stringRedisTemplate);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.increment(anyString())).thenReturn(1L);

    service.checkAllowed(1L, 7L);

    verify(stringRedisTemplate).expire(
        org.mockito.ArgumentMatchers.startsWith("seckill:rate-limit:1:7:"),
        eq(Duration.ofMinutes(1)));
  }

  @Test
  void checkAllowed_shouldRejectRequestWhenCountExceedsLimit() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    SeckillRateLimitService service = new SeckillRateLimitService(stringRedisTemplate);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.increment(anyString())).thenReturn(6L);

    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> service.checkAllowed(1L, 7L));

    assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatusCode());
    assertEquals("请求过于频繁，请稍后再试", exception.getReason());
  }

  @Test
  void checkAllowed_shouldUseIndependentKeysForDifferentUsers() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    @SuppressWarnings("unchecked")
    ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    SeckillRateLimitService service = new SeckillRateLimitService(stringRedisTemplate);
    when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    when(valueOperations.increment(anyString())).thenReturn(1L);

    service.checkAllowed(1L, 7L);
    service.checkAllowed(1L, 8L);

    ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
    verify(valueOperations, org.mockito.Mockito.times(2)).increment(keyCaptor.capture());
    assertEquals(2, keyCaptor.getAllValues().stream().distinct().count());
  }
}
