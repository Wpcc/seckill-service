package com.wpcc.seckillservice.seckill;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wpcc.seckillservice.seckill.dto.SeckillActivityResponse;

@Service
public class SeckillActivityService {
  private final SeckillActivityMapper seckillActivityMapper;

  public SeckillActivityService(
      SeckillActivityMapper seckillActivityMapper) {
    this.seckillActivityMapper = seckillActivityMapper;
  }

  public Optional<SeckillActivityResponse> findById(
      Long id) {
    return seckillActivityMapper.findById(id).map(this::toResponse);
  }

  private SeckillActivityResponse toResponse(
      SeckillActivity seckillActivity) {
    return new SeckillActivityResponse(seckillActivity.getId(), seckillActivity.getProductId(),
        seckillActivity.getSeckillPrice(), seckillActivity.getSeckillStock(), seckillActivity.getStartTime(),
        seckillActivity.getEndTime(), seckillActivity.getStatus());
  }
}
