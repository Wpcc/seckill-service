package com.wpcc.seckillservice.seckill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SeckillActivity {
  private Long id;
  private Long productId;
  private BigDecimal seckillPrice;
  private Integer seckillStock;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Byte status;

  public SeckillActivity() {

  }

  public SeckillActivity(
      Long productId,
      BigDecimal seckillPrice,
      Integer seckillStock,
      LocalDateTime startTime,
      LocalDateTime endTime,
      Byte status) {
    this.productId = productId;
    this.seckillPrice = seckillPrice;
    this.seckillStock = seckillStock;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(
      Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(
      Long productId) {
    this.productId = productId;
  }

  public BigDecimal getSeckillPrice() {
    return seckillPrice;
  }

  public void setSeckillPrice(
      BigDecimal seckillPrice) {
    this.seckillPrice = seckillPrice;
  }

  public Integer getSeckillStock() {
    return seckillStock;
  }

  public void setSeckillStock(
      Integer seckillStock) {
    this.seckillStock = seckillStock;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(
      LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(
      LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public Byte getStatus() {
    return status;
  }

  public void setStatus(
      Byte status) {
    this.status = status;
  }

}
