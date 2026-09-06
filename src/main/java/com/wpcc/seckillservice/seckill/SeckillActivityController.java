package com.wpcc.seckillservice.seckill;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wpcc.seckillservice.seckill.dto.SeckillActivityResponse;

@RestController
@RequestMapping("/api/seckill-activities")
public class SeckillActivityController {
  private final SeckillActivityService seckillActivityService;

  public SeckillActivityController(
      SeckillActivityService seckillActivityService) {
    this.seckillActivityService = seckillActivityService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<SeckillActivityResponse> findById(
      @PathVariable
      Long id) {
    return seckillActivityService.findById(id).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
