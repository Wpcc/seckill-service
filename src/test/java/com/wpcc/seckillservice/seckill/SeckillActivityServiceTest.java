package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.wpcc.seckillservice.seckill.dto.SeckillActivityResponse;

class SeckillActivityServiceTest {

  @Test
  void findById_shouldMapActivityToResponse() {
    SeckillActivityMapper seckillActivityMapper = mock(SeckillActivityMapper.class);
    SeckillActivityService seckillActivityService = new SeckillActivityService(seckillActivityMapper);
    LocalDateTime startTime = LocalDateTime.of(2026, 9, 10, 10, 0);
    LocalDateTime endTime = LocalDateTime.of(2026, 9, 10, 10, 30);
    SeckillActivity activity = new SeckillActivity(
        1L,
        new BigDecimal("5999.00"),
        10,
        startTime,
        endTime,
        (byte) 1);
    activity.setId(1L);

    when(seckillActivityMapper.findById(1L)).thenReturn(Optional.of(activity));

    Optional<SeckillActivityResponse> response = seckillActivityService.findById(1L);

    assertTrue(response.isPresent());
    assertEquals(1L, response.orElseThrow().id());
    assertEquals(1L, response.orElseThrow().productId());
    assertEquals(new BigDecimal("5999.00"), response.orElseThrow().seckillPrice());
    assertEquals(startTime, response.orElseThrow().startTime());
  }

  @Test
  void findById_shouldReturnEmptyWhenActivityDoesNotExist() {
    SeckillActivityMapper seckillActivityMapper = mock(SeckillActivityMapper.class);
    SeckillActivityService seckillActivityService = new SeckillActivityService(seckillActivityMapper);

    when(seckillActivityMapper.findById(999L)).thenReturn(Optional.empty());

    assertTrue(seckillActivityService.findById(999L).isEmpty());
  }
}
