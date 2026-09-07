package com.wpcc.seckillservice.seckill;

import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.auth.CurrentUserContext;
import com.wpcc.seckillservice.seckill.dto.SeckillOrderResponse;

@Service
public class SeckillOrderApplicationService {
  private final SeckillEligibilityService seckillEligibilityService;
  private final SeckillOrderService seckillOrderService;

  public SeckillOrderApplicationService(
      SeckillEligibilityService seckillEligibilityService,
      SeckillOrderService seckillOrderService) {
    this.seckillEligibilityService = seckillEligibilityService;
    this.seckillOrderService = seckillOrderService;
  }

  public SeckillOrderResponse createOrder(
      Long activityId) {
    seckillEligibilityService.check(activityId);

    Long userId = CurrentUserContext.requireUserId();
    SeckillOrder order = seckillOrderService.createOrder(activityId, userId);

    return new SeckillOrderResponse(order.getId(), order.getActivityId(), order.getProductId(), order.getSeckillPrice(),
        "SUCCESS");
  }
}
