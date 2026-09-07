package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.wpcc.seckillservice.auth.CurrentUserContext;
import com.wpcc.seckillservice.seckill.dto.SeckillCheckResponse;
import com.wpcc.seckillservice.seckill.dto.SeckillOrderResponse;

class SeckillOrderApplicationServiceTest {

  @AfterEach
  void clearCurrentUser() {
    CurrentUserContext.clear();
  }

  @Test
  void createOrder_shouldCheckEligibilityThenCreateOrderForCurrentUser() {
    SeckillEligibilityService eligibilityService = mock(SeckillEligibilityService.class);
    SeckillOrderService orderService = mock(SeckillOrderService.class);
    SeckillOrderApplicationService service = new SeckillOrderApplicationService(eligibilityService, orderService);
    SeckillOrder order = new SeckillOrder(1L, 100L, 20L, new BigDecimal("9.90"));
    order.setId(10L);
    CurrentUserContext.setUserId(100L);

    when(eligibilityService.check(1L))
        .thenReturn(new SeckillCheckResponse(1L, 20L, 100L, new BigDecimal("9.90")));
    when(orderService.createOrder(1L, 100L)).thenReturn(order);

    SeckillOrderResponse response = service.createOrder(1L);

    assertEquals(10L, response.orderId());
    assertEquals(1L, response.activityId());
    assertEquals(20L, response.productId());
    assertEquals(new BigDecimal("9.90"), response.seckillPrice());
    assertEquals("SUCCESS", response.status());
    InOrder inOrder = inOrder(eligibilityService, orderService);
    inOrder.verify(eligibilityService).check(1L);
    inOrder.verify(orderService).createOrder(1L, 100L);
  }
}
