package com.wpcc.seckillservice.seckill;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SeckillOrderService {
  private final SeckillActivityMapper seckillActivityMapper;
  private final SeckillOrderMapper seckillOrderMapper;
  private final SeckillStockService seckillStockService;

  public SeckillOrderService(
      SeckillActivityMapper seckillActivityMapper,
      SeckillOrderMapper seckillOrderMapper,
      SeckillStockService seckillStockService) {
    this.seckillActivityMapper = seckillActivityMapper;
    this.seckillOrderMapper = seckillOrderMapper;
    this.seckillStockService = seckillStockService;
  }

  @Transactional
  public SeckillOrder createOrder(
      Long activityId,
      Long userId) {
    SeckillActivity activity = seckillActivityMapper.findById(activityId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "秒杀活动不存在"));

    validateActivity(activity);

    if (seckillOrderMapper.findByActivityIdAndUserId(activityId, userId).isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "请勿重复参加同一场秒杀活动");
    }

    boolean stockDecreased = seckillStockService.tryDecreaseStock(activityId);
    if (!stockDecreased) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "秒杀库存不足");
    }

    SeckillOrder order = new SeckillOrder(activityId, userId, activity.getProductId(), activity.getSeckillPrice());

    int affectedRows = seckillOrderMapper.insert(order);
    if (affectedRows != 1) {
      throw new IllegalStateException("创建秒杀订单失败");
    }

    return order;
  }

  private void validateActivity(
      SeckillActivity activity) {
    LocalDateTime now = LocalDateTime.now();

    boolean isActive = Byte.valueOf((byte) 1).equals(activity.getStatus()) && !now.isBefore(activity.getStartTime())
        && now.isBefore(activity.getEndTime());

    if (!isActive) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "当前秒杀活动不可用");
    }
  }
}
