package com.wpcc.seckillservice.seckill;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.seckillservice.seckill.dto.SeckillActivityResponse;
import com.wpcc.seckillservice.seckill.dto.SeckillCheckResponse;

@RestController
@RequestMapping("/api/seckill-activities")
public class SeckillActivityController {
  private final SeckillActivityService seckillActivityService;
  private final SeckillEligibilityService seckillEligibilityService;

  public SeckillActivityController(
      SeckillActivityService seckillActivityService,
      SeckillEligibilityService seckillEligibilityService) {
    this.seckillActivityService = seckillActivityService;
    this.seckillEligibilityService = seckillEligibilityService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<SeckillActivityResponse> findById(
      @PathVariable
      Long id) {
    return seckillActivityService.findById(id).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping("/{activityId}/check")
  public ResponseEntity<SeckillCheckResponse> check(
      @PathVariable
      Long activityId) {
    return ResponseEntity.ok(seckillEligibilityService.check(activityId));
  }
}
