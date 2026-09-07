package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class SeckillOrderServiceTest {

  @Test
  void createOrder_shouldDecreaseStockAndCreateOrderForActiveActivity() {
    SeckillActivityMapper activityMapper = mock(SeckillActivityMapper.class);
    SeckillOrderMapper orderMapper = mock(SeckillOrderMapper.class);
    SeckillStockService stockService = mock(SeckillStockService.class);
    SeckillOrderService service = new SeckillOrderService(activityMapper, orderMapper, stockService);
    SeckillActivity activity = activeActivity();

    when(activityMapper.findById(1L)).thenReturn(Optional.of(activity));
    when(orderMapper.findByActivityIdAndUserId(1L, 100L)).thenReturn(Optional.empty());
    when(stockService.tryDecreaseStock(1L)).thenReturn(true);
    doAnswer(invocation -> {
      SeckillOrder order = invocation.getArgument(0);
      order.setId(10L);
      return 1;
    }).when(orderMapper).insert(any(SeckillOrder.class));

    SeckillOrder order = service.createOrder(1L, 100L);

    assertEquals(10L, order.getId());
    assertEquals(1L, order.getActivityId());
    assertEquals(100L, order.getUserId());
    assertEquals(20L, order.getProductId());
    assertEquals(new BigDecimal("9.90"), order.getSeckillPrice());
    verify(stockService).tryDecreaseStock(1L);
    verify(orderMapper).insert(order);
  }

  @Test
  void createOrder_shouldRejectDuplicateOrderBeforeDecreasingStock() {
    SeckillActivityMapper activityMapper = mock(SeckillActivityMapper.class);
    SeckillOrderMapper orderMapper = mock(SeckillOrderMapper.class);
    SeckillStockService stockService = mock(SeckillStockService.class);
    SeckillOrderService service = new SeckillOrderService(activityMapper, orderMapper, stockService);

    when(activityMapper.findById(1L)).thenReturn(Optional.of(activeActivity()));
    when(orderMapper.findByActivityIdAndUserId(1L, 100L))
        .thenReturn(Optional.of(new SeckillOrder()));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.createOrder(1L, 100L));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(stockService, never()).tryDecreaseStock(1L);
    verify(orderMapper, never()).insert(any(SeckillOrder.class));
  }

  @Test
  void createOrder_shouldRejectWhenDatabaseStockIsInsufficient() {
    SeckillActivityMapper activityMapper = mock(SeckillActivityMapper.class);
    SeckillOrderMapper orderMapper = mock(SeckillOrderMapper.class);
    SeckillStockService stockService = mock(SeckillStockService.class);
    SeckillOrderService service = new SeckillOrderService(activityMapper, orderMapper, stockService);

    when(activityMapper.findById(1L)).thenReturn(Optional.of(activeActivity()));
    when(orderMapper.findByActivityIdAndUserId(1L, 100L)).thenReturn(Optional.empty());
    when(stockService.tryDecreaseStock(1L)).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.createOrder(1L, 100L));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    verify(orderMapper, never()).insert(any(SeckillOrder.class));
  }

  @Test
  void createOrder_shouldRejectInactiveActivityBeforeQueryingOrder() {
    SeckillActivityMapper activityMapper = mock(SeckillActivityMapper.class);
    SeckillOrderMapper orderMapper = mock(SeckillOrderMapper.class);
    SeckillStockService stockService = mock(SeckillStockService.class);
    SeckillOrderService service = new SeckillOrderService(activityMapper, orderMapper, stockService);
    SeckillActivity activity = activeActivity();
    activity.setStatus((byte) 0);
    when(activityMapper.findById(1L)).thenReturn(Optional.of(activity));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.createOrder(1L, 100L));

    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    verify(orderMapper, never()).findByActivityIdAndUserId(1L, 100L);
    verify(stockService, never()).tryDecreaseStock(1L);
  }

  @Test
  void createOrder_shouldReturnNotFoundWhenActivityDoesNotExist() {
    SeckillActivityMapper activityMapper = mock(SeckillActivityMapper.class);
    SeckillOrderMapper orderMapper = mock(SeckillOrderMapper.class);
    SeckillStockService stockService = mock(SeckillStockService.class);
    SeckillOrderService service = new SeckillOrderService(activityMapper, orderMapper, stockService);
    when(activityMapper.findById(999L)).thenReturn(Optional.empty());

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.createOrder(999L, 100L));

    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    verify(stockService, never()).tryDecreaseStock(999L);
  }

  private SeckillActivity activeActivity() {
    return new SeckillActivity(
        20L,
        new BigDecimal("9.90"),
        10,
        LocalDateTime.now().minusMinutes(1),
        LocalDateTime.now().plusMinutes(1),
        (byte) 1);
  }
}
