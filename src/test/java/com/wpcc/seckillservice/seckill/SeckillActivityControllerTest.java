package com.wpcc.seckillservice.seckill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wpcc.seckillservice.seckill.dto.SeckillOrderResponse;

class SeckillActivityControllerTest {

  @Test
  void createOrder_shouldReturnCreatedResponse() {
    SeckillActivityService activityService = mock(SeckillActivityService.class);
    SeckillEligibilityService eligibilityService = mock(SeckillEligibilityService.class);
    SeckillOrderApplicationService orderApplicationService = mock(SeckillOrderApplicationService.class);
    SeckillActivityController controller = new SeckillActivityController(
        activityService,
        eligibilityService,
        orderApplicationService);
    SeckillOrderResponse expected = new SeckillOrderResponse(
        10L,
        1L,
        20L,
        new BigDecimal("9.90"),
        "SUCCESS");
    when(orderApplicationService.createOrder(1L)).thenReturn(expected);

    ResponseEntity<SeckillOrderResponse> response = controller.createOrder(1L);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expected, response.getBody());
  }
}
