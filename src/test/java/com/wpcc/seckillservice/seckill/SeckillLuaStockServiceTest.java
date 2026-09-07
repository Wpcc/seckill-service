package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

class SeckillLuaStockServiceTest {

  @Test
  void trySeckill_shouldReturnSuccessWhenScriptReturnsZero() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    DefaultRedisScript<Long> script = mockScript();
    SeckillLuaStockService service = new SeckillLuaStockService(stringRedisTemplate, script);

    when(stringRedisTemplate.execute(
        eq(script),
        eq(List.of("seckill:stock:1", "seckill:purchased-users:1")),
        eq("100"),
        eq("1800"))).thenReturn(0L);

    SeckillLuaStockService.SeckillLuaStockResult result =
        service.trySeckill(1L, 100L, Duration.ofMinutes(30));

    assertEquals(SeckillLuaStockService.SeckillLuaStockResult.SUCCESS, result);
    verify(stringRedisTemplate).execute(
        eq(script),
        eq(List.of("seckill:stock:1", "seckill:purchased-users:1")),
        eq("100"),
        eq("1800"));
  }

  @Test
  void trySeckill_shouldReturnOutOfStockWhenScriptReturnsOne() {
    assertEquals(SeckillLuaStockService.SeckillLuaStockResult.OUT_OF_STOCK,
        executeWithResult(1L));
  }

  @Test
  void trySeckill_shouldReturnDuplicatePurchaseWhenScriptReturnsTwo() {
    assertEquals(SeckillLuaStockService.SeckillLuaStockResult.DUPLICATE_PURCHASE,
        executeWithResult(2L));
  }

  @Test
  void trySeckill_shouldThrowWhenScriptReturnsNull() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    SeckillLuaStockService service = new SeckillLuaStockService(stringRedisTemplate, mockScript());

    assertThrows(IllegalStateException.class,
        () -> service.trySeckill(1L, 100L, Duration.ofMinutes(30)));
  }

  @Test
  void trySeckill_shouldThrowWhenScriptReturnsUnknownCode() {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    DefaultRedisScript<Long> script = mockScript();
    SeckillLuaStockService service = new SeckillLuaStockService(stringRedisTemplate, script);
    when(stringRedisTemplate.execute(eq(script), eq(List.of("seckill:stock:1", "seckill:purchased-users:1")),
        eq("100"), eq("1800"))).thenReturn(99L);

    assertThrows(IllegalStateException.class,
        () -> service.trySeckill(1L, 100L, Duration.ofMinutes(30)));
  }

  private SeckillLuaStockService.SeckillLuaStockResult executeWithResult(Long scriptResult) {
    StringRedisTemplate stringRedisTemplate = mock(StringRedisTemplate.class);
    DefaultRedisScript<Long> script = mockScript();
    SeckillLuaStockService service = new SeckillLuaStockService(stringRedisTemplate, script);
    when(stringRedisTemplate.execute(eq(script), eq(List.of("seckill:stock:1", "seckill:purchased-users:1")),
        eq("100"), eq("1800"))).thenReturn(scriptResult);

    return service.trySeckill(1L, 100L, Duration.ofMinutes(30));
  }

  @SuppressWarnings("unchecked")
  private DefaultRedisScript<Long> mockScript() {
    return mock(DefaultRedisScript.class);
  }
}
